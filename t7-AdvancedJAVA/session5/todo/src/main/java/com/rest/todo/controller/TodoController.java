package com.rest.todo.controller;

import com.rest.todo.dto.TodoRequestDto;
import com.rest.todo.dto.TodoResponseDto;
import com.rest.todo.entity.TodoStatus;
import com.rest.todo.service.TodoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    //1. POST /todos
    //● Accept data using @RequestBody + @Valid
    //● Set createdAt automatically (not from user)
    //● Default status = PENDING if not provided
    @PostMapping
    public TodoResponseDto createTodo(@Valid @RequestBody TodoRequestDto todoRequestDto) {
        logger.info("Received request to create todo with title='{}'", todoRequestDto.getTitle());
        TodoResponseDto createdTodo = todoService.createTodo(todoRequestDto);
        logger.info("Successfully created todo with id={}", createdTodo.getId());
        return createdTodo;
    }

    //2. Get All TODOs
    //GET /todos
    //● Return list of all tasks
    @GetMapping
    public ResponseEntity<List<TodoResponseDto>> getAllTodos() {
        logger.info("Received request to fetch all todos");
        List<TodoResponseDto> todos = todoService.getAllTodos();
        logger.info("Fetched {} todos", todos.size());
        return ResponseEntity.ok(todos);
    }

    //3. Get TODO by ID
    //GET /todos/{id}
    //● Use @PathVariable
    //● Handle case if TODO not found
    @GetMapping("/{id}")
    public TodoResponseDto getTodoById(@PathVariable Long id) {
        logger.info("Received request to fetch todo with id={}", id);
        TodoResponseDto todo = todoService.getTodoById(id);
        logger.info("Successfully fetched todo with id={}", id);
        return todo;
    }

    //4. Update TODO
    //PUT /todos/{id}
    //● Allow updating:
    //○ title
    //○ description
    //○ status
    @PutMapping("/{id}")
    public TodoResponseDto updateTodo(@PathVariable Long id, @Valid @RequestBody TodoRequestDto todoRequestDto) {
        logger.info("Received request to update todo with id={}", id);
        TodoResponseDto updatedTodo = todoService.updateTodo(id, todoRequestDto);
        logger.info("Successfully updated todo with id={}", id);
        return updatedTodo;
    }

    //5. Delete TODO
    //DELETE /todos/{id}
    //● Delete task by ID
    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable Long id) {
        logger.info("Received request to delete todo with id={}", id);
        todoService.deleteTodo(id);
        logger.info("Successfully deleted todo with id={}", id);
        return "Task Deleted Successfully";
    }

    //Allowed transitions:
    //○ PENDING → COMPLETED
    //○ COMPLETED → PENDING
    @PatchMapping("/{id}/status")
    public String updateTodoStatus(@PathVariable Long id, @RequestParam TodoStatus newStatus) {
        logger.info("Received request to update status for todo id={} to {}", id, newStatus);
        todoService.updateTodoStatus(id, newStatus);
        logger.info("Successfully updated status for todo id={} to {}", id, newStatus);
        return "Task status updated to " + newStatus;
    }
}
