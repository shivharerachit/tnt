package com.project.ReimbursementPortal.dto;

import com.project.ReimbursementPortal.entity.UserRole;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserRequestDto {



    @NotBlank(message = "Name is required")
    private String name;

    @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required and must be one of: ADMIN, MANAGER, EMPLOYEE")
    private UserRole role;

    @Nullable
    private Long managerId;

    @Nullable
    private List<Long> reporteesIds;
}
