package com.rest.todo.service;

import com.rest.todo.dto.TodoRequestDto;
import com.rest.todo.dto.TodoResponseDto;
import com.rest.todo.entity.Todo;
import com.rest.todo.mapping.TodoMapping;
import com.rest.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoResponseDto createTodo(final TodoRequestDto todoRequest) {
        Todo todo = new Todo();
        todo.setTitle(todoRequest.getTitle());
        todo.setDescription(todoRequest.getDescription());
        todo.setStatus(todoRequest.getStatus());

        Todo savedTodo = todoRepository.save(todo);
        return TodoMapping.convertToDTO(savedTodo);
    }

    public List<TodoResponseDto> getAllTodos() {
        return todoRepository.findAll()
                .stream()
                .map(TodoMapping::convertToDTO)
                .collect(Collectors.toList());
    }

    public TodoResponseDto getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        return TodoMapping.convertToDTO(todo);
    }

    public TodoResponseDto updateTodo(Long id, TodoRequestDto todoRequestDto) {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));

        existingTodo.setTitle(todoRequestDto.getTitle());
        existingTodo.setDescription(todoRequestDto.getDescription());
        existingTodo.setStatus(todoRequestDto.getStatus());

        Todo updatedTodo = todoRepository.save(existingTodo);
        return TodoMapping.convertToDTO(updatedTodo);
    }

    public void deleteTodo(Long id) {
        todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        todoRepository.deleteById(id);
    }
}
