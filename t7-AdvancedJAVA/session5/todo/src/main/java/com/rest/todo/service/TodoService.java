package com.rest.todo.service;

import com.rest.todo.dto.TodoRequestDto;
import com.rest.todo.dto.TodoResponseDto;
import com.rest.todo.entity.Todo;
import com.rest.todo.entity.TodoStatus;
import com.rest.todo.mapping.TodoMapping;
import com.rest.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;
    private final NotificationServiceClient notificationServiceClient;

    public TodoService(TodoRepository todoRepository, NotificationServiceClient notificationServiceClient) {
        this.todoRepository = todoRepository;
        this.notificationServiceClient = notificationServiceClient;
    }

    public TodoResponseDto createTodo(final TodoRequestDto todoRequest) {
        logger.info("Creating todo with title='{}'", todoRequest.getTitle());
        Todo todo = new Todo();
        todo.setTitle(todoRequest.getTitle());
        todo.setDescription(todoRequest.getDescription());
        todo.setStatus(todoRequest.getStatus());

        Todo savedTodo = todoRepository.save(todo);
        notificationServiceClient.sendTodoCreatedNotification(savedTodo);
        logger.info("Todo created successfully with id={}", savedTodo.getId());
        return TodoMapping.convertToDTO(savedTodo);
    }

    public List<TodoResponseDto> getAllTodos() {
        logger.info("Fetching all todos");
        return todoRepository.findAll()
                .stream()
                .map(TodoMapping::convertToDTO)
                .collect(Collectors.toList());
    }

    public TodoResponseDto getTodoById(Long id) {
        logger.info("Fetching todo with id={}", id);
        Todo todo = findTodoByIdOrThrow(id);

        return TodoMapping.convertToDTO(todo);
    }

    public TodoResponseDto updateTodo(Long id, TodoRequestDto todoRequestDto) {
        logger.info("Updating todo with id={}", id);
        Todo existingTodo = findTodoByIdOrThrow(id);

        existingTodo.setTitle(todoRequestDto.getTitle());
        existingTodo.setDescription(todoRequestDto.getDescription());
        existingTodo.setStatus(todoRequestDto.getStatus());

        Todo updatedTodo = todoRepository.save(existingTodo);
        logger.info("Todo updated successfully with id={}", id);
        return TodoMapping.convertToDTO(updatedTodo);
    }

    public void deleteTodo(Long id) {
        logger.info("Deleting todo with id={}", id);
        findTodoByIdOrThrow(id);
        todoRepository.deleteById(id);
        logger.info("Todo deleted successfully with id={}", id);
    }

    public void updateTodoStatus(Long id, TodoStatus newStatus) {
        logger.info("Updating todo status for id={} to {}", id, newStatus);
        Todo existingTodo = findTodoByIdOrThrow(id);
        existingTodo.setStatus(newStatus);
        todoRepository.save(existingTodo);
        logger.info("Todo status updated successfully for id={} to {}", id, newStatus);
    }

    private Todo findTodoByIdOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Todo not found with id={}", id);
                    return new RuntimeException("Todo not found with id: " + id);
                });
    }
}
