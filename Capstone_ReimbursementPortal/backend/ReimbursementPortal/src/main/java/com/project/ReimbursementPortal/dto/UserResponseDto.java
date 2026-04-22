package com.project.ReimbursementPortal.dto;

import com.project.ReimbursementPortal.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private Long managerId;
    private List<Long> reporteesIds;
    private LocalDateTime createdAt;
}
