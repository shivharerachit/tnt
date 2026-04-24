package com.rest.todo.mapping;

import com.rest.todo.dto.TodoResponseDto;
import com.rest.todo.entity.Todo;

public class TodoMapping {
    public static TodoResponseDto convertToDTO(Todo todo) {
        TodoResponseDto dto = new TodoResponseDto(
            todo.getId(),
            todo.getTitle(),
            todo.getDescription(),
            todo.getStatus(),
            todo.getCreatedAt()
        );
        return dto;
    }
}
