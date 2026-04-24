package com.rest.todo.controller;

import com.rest.todo.dto.TodoRequestDto;
import com.rest.todo.dto.TodoResponseDto;
import com.rest.todo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    //1. POST /todos
    //● Accept data using @RequestBody + @Valid
    //● Set createdAt automatically (not from user)
    //● Default status = PENDING if not provided
    @PostMapping
    public TodoResponseDto createTodo(@RequestBody TodoRequestDto todoRequestDto) {
        return todoService.createTodo(todoRequestDto);
    }

    //2. Get All TODOs
    //GET /todos
    //● Return list of all tasks
    @GetMapping
    public ResponseEntity<List<TodoResponseDto>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    //3. Get TODO by ID
    //GET /todos/{id}
    //● Use @PathVariable
    //● Handle case if TODO not found
    @GetMapping("/{id}")
    public TodoResponseDto getTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id);
    }

    //4. Update TODO
    //PUT /todos/{id}
    //● Allow updating:
    //○ title
    //○ description
    //○ status
    @PutMapping("/{id}")
    public TodoResponseDto updateTodo(@PathVariable Long id, @RequestBody TodoRequestDto todoRequestDto) {
        return todoService.updateTodo(id, todoRequestDto);
    }

    //5. Delete TODO
    //DELETE /todos/{id}
    //● Delete task by ID
    //Allowed transitions:
    //○ PENDING → COMPLETED
    //○ COMPLETED → PENDING
    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }
}
