// Path: /home/prrrssshhh/PROJECTS/primetrade.AI/src/main/java/com/primetrade/api/controller/TaskController.java
package com.primetrade.api.controller;

import com.primetrade.api.dto.TaskRequest;
import com.primetrade.api.model.Task;
import com.primetrade.api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Get tasks for logged in user (or all tasks for admin)")
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getAllTasks(authentication.getName()));
    }

    @Operation(summary = "Create task for logged in user")
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest request, Authentication authentication) {
        Task created = taskService.createTask(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update task by id (owner or admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @Valid @RequestBody TaskRequest request,
                                           Authentication authentication) {
        return ResponseEntity.ok(taskService.updateTask(id, authentication.getName(), request));
    }

    @Operation(summary = "Delete task by id (owner or admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        taskService.deleteTask(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
