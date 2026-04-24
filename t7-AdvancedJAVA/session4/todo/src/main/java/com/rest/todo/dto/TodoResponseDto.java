package com.rest.todo.dto;

import com.rest.todo.entity.TodoStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.valueextraction.UnwrapByDefault;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TodoResponseDto {
    private Long id;

    @NotNull(message = "Title cannot be null")
    @Size(min = 3, message = "Title must have minimum 3 characters")
    private String title;

    @NotNull(message = "Description cannot be null")
    private String description;

    private TodoStatus status;

    private LocalDateTime createdAt;
}
