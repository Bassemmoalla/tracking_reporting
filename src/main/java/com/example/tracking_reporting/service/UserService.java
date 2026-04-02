package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.AssignPermissionGroupToUserRequest;
import com.example.tracking_reporting.dto.CreateUserRequest;
import com.example.tracking_reporting.dto.UpdateUserRequest;
import com.example.tracking_reporting.dto.UserResponse;
import com.example.tracking_reporting.entity.PermissionGroup;
import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.entity.UserStatus;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.repository.PermissionGroupRepository;
import com.example.tracking_reporting.repository.TeamAssignmentRepository;
import com.example.tracking_reporting.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private final TeamAssignmentRepository teamAssignmentRepository;

    public UserService(UserRepository userRepository,
                       PermissionGroupRepository permissionGroupRepository,
                       TeamAssignmentRepository teamAssignmentRepository) {
        this.userRepository = userRepository;
        this.permissionGroupRepository = permissionGroupRepository;
        this.teamAssignmentRepository = teamAssignmentRepository;
    }

    public UserResponse create(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setEmail(request.email());
        user.setCin(request.cin());
        user.setStatus(UserStatus.valueOf(request.status().toUpperCase()));
        return map(userRepository.save(user));
    }

    @Transactional
    public UserResponse assignPermissionGroup(AssignPermissionGroupToUserRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        PermissionGroup group = permissionGroupRepository.findById(request.permissionGroupId())
                .orElseThrow(() -> new RuntimeException("Permission group not found"));

        user.getPermissionGroups().add(group);
        userRepository.saveAndFlush(user);
        return map(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return map(user);
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setEmail(request.email());
        user.setCin(request.cin());
        user.setStatus(UserStatus.valueOf(request.status().toUpperCase()));

        return map(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // remove team assignments (FK)
        teamAssignmentRepository.deleteAllByUser_Id(id);

        // detach permission groups (join table)
        user.getPermissionGroups().clear();
        userRepository.saveAndFlush(user);

        userRepository.delete(user);
    }

    private UserResponse map(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCin(),
                user.getStatus().name(),
                user.getPermissionGroups().stream()
                        .map(PermissionGroup::getName)
                        .collect(Collectors.toSet())
        );
    }
    public User getEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
