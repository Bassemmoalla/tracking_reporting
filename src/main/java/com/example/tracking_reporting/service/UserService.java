package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.AssignPermissionGroupToUserRequest;
import com.example.tracking_reporting.dto.CreateManagedUserAccountRequest;
import com.example.tracking_reporting.dto.CreateUserRequest;
import com.example.tracking_reporting.dto.ManagedUserAccountResponse;
import com.example.tracking_reporting.dto.UpdateUserRequest;
import com.example.tracking_reporting.dto.UserResponse;
import com.example.tracking_reporting.entity.Permission;
import com.example.tracking_reporting.entity.PermissionGroup;
import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.entity.UserStatus;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.repository.PermissionGroupRepository;
import com.example.tracking_reporting.repository.PermissionRepository;
import com.example.tracking_reporting.repository.TeamAssignmentRepository;
import com.example.tracking_reporting.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String USER_NOT_FOUND = "User not found";
    private static final String PERMISSION_GROUP_NOT_FOUND = "Permission group not found";
    private static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private final TeamAssignmentRepository teamAssignmentRepository;
    private final KeycloakUserProvisioningService keycloakUserProvisioningService;
    private final AccountMailService accountMailService;
    private final UserAccessProfileService userAccessProfileService;

    public UserService(UserRepository userRepository,
                       PermissionRepository permissionRepository,
                       PermissionGroupRepository permissionGroupRepository,
                       TeamAssignmentRepository teamAssignmentRepository,
                       KeycloakUserProvisioningService keycloakUserProvisioningService,
                       AccountMailService accountMailService,
                       UserAccessProfileService userAccessProfileService) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.permissionGroupRepository = permissionGroupRepository;
        this.teamAssignmentRepository = teamAssignmentRepository;
        this.keycloakUserProvisioningService = keycloakUserProvisioningService;
        this.accountMailService = accountMailService;
        this.userAccessProfileService = userAccessProfileService;
    }

    public UserResponse create(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setEmail(request.email());
        user.setCin(request.cin());
        user.setStatus(parseStatus(request.status()));
        return map(userRepository.save(user));
    }

    @Transactional
    public ManagedUserAccountResponse createManagedAccount(CreateManagedUserAccountRequest request) {
        validateUniqueUser(request.email(), request.cin());

        List<String> directPermissionKeys = cleanPermissionKeys(request.permissionKeys());
        List<PermissionGroup> permissionGroups = resolvePermissionGroups(request.permissionGroupIds());

        Set<String> effectivePermissionKeySet = new LinkedHashSet<>(directPermissionKeys);
        for (PermissionGroup permissionGroup : permissionGroups) {
            permissionGroup.getPermissions().stream()
                    .map(Permission::getPermissionKey)
                    .forEach(effectivePermissionKeySet::add);
        }

        if (effectivePermissionKeySet.isEmpty()) {
            throw new IllegalArgumentException("At least one permission key or permission group id is required");
        }

        List<String> effectivePermissionKeys = List.copyOf(effectivePermissionKeySet);
        List<Permission> effectivePermissions = resolvePermissions(effectivePermissionKeys);
        UserStatus status = parseStatus(request.status());

        String keycloakUserId = null;

        try {
            keycloakUserId = keycloakUserProvisioningService.createUser(
                    request.email(),
                    request.firstName(),
                    request.lastName(),
                    request.password(),
                    status == UserStatus.ACTIVE,
                    effectivePermissionKeys,
                    false
            );

            User user = new User();
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setPhoneNumber(request.phoneNumber().trim());
            user.setEmail(request.email().trim());
            user.setCin(request.cin().trim());
            user.setStatus(status);
            user.setKeycloakId(keycloakUserId);
            user.setPermissionGroups(new HashSet<>(permissionGroups));
            user.setPermissions(new HashSet<>(effectivePermissions));

            User savedUser = userRepository.save(user);

            accountMailService.sendAccountCreatedEmail(
                    savedUser.getEmail(),
                    savedUser.getFirstName(),
                    savedUser.getEmail(),
                    request.password()
            );

            return mapManaged(savedUser);
        } catch (Exception ex) {
            if (keycloakUserId != null && !keycloakUserId.isBlank()) {
                try {
                    keycloakUserProvisioningService.deleteUser(keycloakUserId);
                } catch (Exception ignored) {
                }
            }
            throw ex;
        }
    }

    @Transactional
    public UserResponse assignPermissionGroup(AssignPermissionGroupToUserRequest request) {
        User user = findUserByIdOrThrow(request.userId());
        PermissionGroup group = findPermissionGroupByIdOrThrow(request.permissionGroupId());

        user.getPermissionGroups().add(group);
        refreshEffectivePermissions(user);
        userRepository.saveAndFlush(user);

        if (user.getKeycloakId() != null && !user.getKeycloakId().isBlank()) {
            keycloakUserProvisioningService.syncUserClientRoles(
                    user.getKeycloakId(),
                    userAccessProfileService.extractPermissionKeys(user).stream().toList()
            );
        }

        return map(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return map(findUserByIdOrThrow(id));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = findUserByIdOrThrow(id);

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setEmail(request.email());
        user.setCin(request.cin());
        user.setStatus(parseStatus(request.status()));

        return map(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        User user = findUserByIdOrThrow(id);

        teamAssignmentRepository.deleteAllByUser_Id(id);

        user.getPermissionGroups().clear();
        user.getPermissions().clear();
        userRepository.saveAndFlush(user);

        if (user.getKeycloakId() != null && !user.getKeycloakId().isBlank()) {
            try {
                keycloakUserProvisioningService.deleteUser(user.getKeycloakId());
            } catch (Exception ignored) {
            }
        }

        userRepository.delete(user);
    }

    public User getEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + id));
    }

    private User findUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
    }

    private PermissionGroup findPermissionGroupByIdOrThrow(UUID id) {
        return permissionGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PERMISSION_GROUP_NOT_FOUND));
    }

    private void validateUniqueUser(String email, String cin) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("A user with this email already exists");
        }
        if (userRepository.existsByCin(cin)) {
            throw new IllegalArgumentException("A user with this CIN already exists");
        }
    }

    private List<String> cleanPermissionKeys(List<String> permissionKeys) {
        if (permissionKeys == null) {
            return List.of();
        }

        return permissionKeys.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    private List<PermissionGroup> resolvePermissionGroups(List<UUID> permissionGroupIds) {
        if (permissionGroupIds == null || permissionGroupIds.isEmpty()) {
            return List.of();
        }

        List<UUID> uniqueIds = permissionGroupIds.stream()
                .filter(id -> id != null)
                .distinct()
                .toList();

        List<PermissionGroup> groups = permissionGroupRepository.findAllById(uniqueIds);

        Set<UUID> foundIds = groups.stream()
                .map(PermissionGroup::getId)
                .collect(Collectors.toSet());

        List<UUID> missingIds = uniqueIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException("Permission groups not found: " + missingIds);
        }

        return groups;
    }

    private List<Permission> resolvePermissions(List<String> permissionKeys) {
        List<Permission> permissions = permissionRepository.findByPermissionKeyIn(permissionKeys);

        Set<String> foundKeys = permissions.stream()
                .map(Permission::getPermissionKey)
                .collect(Collectors.toSet());

        Set<String> missingKeys = new LinkedHashSet<>(permissionKeys);
        missingKeys.removeAll(foundKeys);

        if (!missingKeys.isEmpty()) {
            throw new ResourceNotFoundException("Permissions not found: " + String.join(", ", missingKeys));
        }

        return permissions;
    }

    private void refreshEffectivePermissions(User user) {
        Set<Permission> effectivePermissions = new LinkedHashSet<>();

        if (user.getPermissions() != null) {
            effectivePermissions.addAll(user.getPermissions());
        }

        if (user.getPermissionGroups() != null) {
            for (PermissionGroup permissionGroup : user.getPermissionGroups()) {
                effectivePermissions.addAll(permissionGroup.getPermissions());
            }
        }

        user.setPermissions(new HashSet<>(effectivePermissions));
    }

    private UserStatus parseStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        return switch (rawStatus.trim().toUpperCase()) {
            case "ACTIVE", "ACTIF" -> UserStatus.ACTIVE;
            case "INACTIVE", "INACTIF" -> UserStatus.INACTIVE;
            default -> throw new IllegalArgumentException("Unsupported status: " + rawStatus);
        };
    }

    private UserResponse map(User user) {
        Set<String> permissionKeys = userAccessProfileService.extractPermissionKeys(user);
        Set<String> permissionGroupNames = userAccessProfileService.extractPermissionGroupNames(user);
        Set<String> roleLabels = userAccessProfileService.resolveRoleLabels(user);

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCin(),
                user.getStatus().name(),
                permissionKeys,
                permissionGroupNames,
                roleLabels,
                permissionGroupNames
        );
    }

    private ManagedUserAccountResponse mapManaged(User user) {
        Set<String> permissionKeys = userAccessProfileService.extractPermissionKeys(user);
        Set<String> permissionGroupNames = userAccessProfileService.extractPermissionGroupNames(user);
        Set<String> roleLabels = userAccessProfileService.resolveRoleLabels(user);

        return new ManagedUserAccountResponse(
                user.getId(),
                user.getKeycloakId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getCin(),
                user.getStatus().name(),
                permissionKeys,
                permissionGroupNames,
                roleLabels,
                user.getCreatedAt()
        );
    }
}