package com.example.tracking_reporting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "cin")
})
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, length = 30)
    private String phoneNumber;

    @Column(nullable = false, length = 190)
    private String email;

    @Column(nullable = false, length = 20)
    private String cin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(unique = true)
    private String keycloakId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;

    @ManyToMany
    @JoinTable(
            name = "user_permission_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_group_id")
    )
    private Set<PermissionGroup> permissionGroups = new HashSet<>();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
