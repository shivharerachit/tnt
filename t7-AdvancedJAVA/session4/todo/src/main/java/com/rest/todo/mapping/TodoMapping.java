package com.rest.todo.mapping;

import com.rest.todo.dto.TodoResponseDto;
import com.rest.todo.entity.Todo;

public class TodoMapping {
    public static TodoResponseDto convertToDTO(Todo todo) {
        TodoResponseDto dto = new TodoResponseDto();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setStatus(todo.getStatus());
        dto.setCreatedAt(todo.getCreatedAt());
        return dto;
    }
}
