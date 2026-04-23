package com.project.ReimbursementPortal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *
     */
    @Column(nullable = false)
    private String name;

    /**
     *
     */
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    /**
     *
     */
    @Column(nullable = false)
    private String password;

    /**
     *
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     *
     */
    @Column(name = "manager_id")
    private Long managerId;

    /**
     *
     */
    @Column(name = "reportees_ids")
    private List<Long> reporteesIds;

    /**
     *
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
