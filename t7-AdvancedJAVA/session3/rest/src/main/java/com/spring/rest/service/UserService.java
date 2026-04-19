package com.spring.rest.service;

import com.spring.rest.dto.UserResponseDto;
import com.spring.rest.entity.UserEntity;
import com.spring.rest.exception.ResourceNotFoundException;
import com.spring.rest.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDto> searchUsers(String name, Integer age, String role) {
        List<UserEntity> users = userRepository.findAll();

        if (name == null && age == null && role == null) {
            return users.stream().map(this::toUserResponseDto).collect(Collectors.toList());
        }

        return users.stream()
                .filter(user -> name == null
                        || user.getName().toLowerCase(Locale.ROOT)
                        .equals(name.toLowerCase(Locale.ROOT)))
                .filter(user -> age == null || user.getAge().equals(age))
                .filter(user -> role == null
                        || user.getRole().name().equalsIgnoreCase(role))
                .map(this::toUserResponseDto)
                .collect(Collectors.toList());
    }

    private UserResponseDto toUserResponseDto(UserEntity user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getAge(), user.getRole().name());
    }

    public String deleteUser(Long id, Boolean confirm) {
        if (confirm == null || !confirm) {
            return "Confirmation required";
        }

        boolean deleted = userRepository.deleteById(id);
        if(!deleted) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        return "User deleted successfully";
    }
}