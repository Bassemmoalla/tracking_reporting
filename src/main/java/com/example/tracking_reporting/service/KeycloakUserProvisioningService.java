package com.example.tracking_reporting.service;

import com.example.tracking_reporting.config.KeycloakAdminProperties;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeycloakUserProvisioningService {

    private final Keycloak keycloakAdminClient;
    private final KeycloakAdminProperties properties;

    public String createUser(String email,
                             String firstName,
                             String lastName,
                             String password,
                             boolean enabled,
                             List<String> permissionKeys,
                             boolean temporaryPassword) {

        RealmResource realm = keycloakAdminClient.realm(properties.targetRealm());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(enabled);
        user.setEmailVerified(true);

        Response response = realm.users().create(user);
        try {
            if (response.getStatus() != 201) {
                String errorBody = safeReadBody(response);
                throw new IllegalStateException("Failed to create Keycloak user: " + response.getStatus() + " - " + errorBody);
            }

            String keycloakUserId = CreatedResponseUtil.getCreatedId(response);

            resetPassword(realm, keycloakUserId, password, temporaryPassword);
            syncUserClientRoles(keycloakUserId, permissionKeys);

            return keycloakUserId;
        } finally {
            response.close();
        }
    }

    public void syncUserClientRoles(String keycloakUserId, List<String> permissionKeys) {
        RealmResource realm = keycloakAdminClient.realm(properties.targetRealm());

        ClientRepresentation clientRepresentation = realm.clients()
                .findByClientId(properties.targetClientId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Keycloak client not found: " + properties.targetClientId()));

        ClientResource clientResource = realm.clients().get(clientRepresentation.getId());

        List<RoleRepresentation> currentRoles = realm.users()
                .get(keycloakUserId)
                .roles()
                .clientLevel(clientRepresentation.getId())
                .listAll();

        if (currentRoles != null && !currentRoles.isEmpty()) {
            realm.users()
                    .get(keycloakUserId)
                    .roles()
                    .clientLevel(clientRepresentation.getId())
                    .remove(currentRoles);
        }

        Set<String> uniqueKeys = new LinkedHashSet<>();
        if (permissionKeys != null) {
            for (String permissionKey : permissionKeys) {
                if (permissionKey != null && !permissionKey.isBlank()) {
                    uniqueKeys.add(permissionKey.trim());
                }
            }
        }

        if (uniqueKeys.isEmpty()) {
            return;
        }

        List<RoleRepresentation> rolesToAssign = new ArrayList<>();
        for (String permissionKey : uniqueKeys) {
            rolesToAssign.add(ensureClientRoleExists(clientResource, permissionKey));
        }

        realm.users()
                .get(keycloakUserId)
                .roles()
                .clientLevel(clientRepresentation.getId())
                .add(rolesToAssign);
    }

    public void deleteUser(String keycloakUserId) {
        keycloakAdminClient.realm(properties.targetRealm())
                .users()
                .delete(keycloakUserId);
    }

    private void resetPassword(RealmResource realm, String keycloakUserId, String password, boolean temporaryPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(temporaryPassword);

        realm.users()
                .get(keycloakUserId)
                .resetPassword(credential);
    }

    private RoleRepresentation ensureClientRoleExists(ClientResource clientResource, String roleName) {
        try {
            return clientResource.roles().get(roleName).toRepresentation();
        } catch (NotFoundException ex) {
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            clientResource.roles().create(role);
            return clientResource.roles().get(roleName).toRepresentation();
        }
    }

    private String safeReadBody(Response response) {
        try {
            return response.readEntity(String.class);
        } catch (Exception ex) {
            return "No response body";
        }
    }
}