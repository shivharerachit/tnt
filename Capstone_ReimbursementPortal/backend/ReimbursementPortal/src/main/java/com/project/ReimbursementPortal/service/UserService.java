package com.project.ReimbursementPortal.service;

import com.project.ReimbursementPortal.config.AppProperties;
import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final AppProperties appProperties;
    private final UserRepository userRepository;

    public UserService(AppProperties appProperties, UserRepository userRepository) {
        this.appProperties = appProperties;
        this.userRepository = userRepository;
    }

    private void validateCompanyEmail(String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        String allowedDomain = appProperties.getAllowedEmailDomain() == null
            ? ""
            : appProperties.getAllowedEmailDomain().trim().toLowerCase();

        if (!normalizedEmail.endsWith(allowedDomain)) {
            throw new IllegalArgumentException("Email must end with " + allowedDomain);
        }
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDTO(user);
    }

    public UserResponseDto createUser(UserRequestDto userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setRole(userRequest.getRole());
        user.setManagerId(userRequest.getManagerId());
        user.setReporteesIds(userRequest.getReporteesIds());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userRepository.deleteById(id);
    }



    public UserResponseDto convertToDTO(User user) {

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getManagerId(),
                user.getReporteesIds(),
                user.getCreatedAt()
        );
    }
}
