package com.rest.todo.dto;

import com.rest.todo.entity.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TodoRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Status is required")
    private TodoStatus status;
}
