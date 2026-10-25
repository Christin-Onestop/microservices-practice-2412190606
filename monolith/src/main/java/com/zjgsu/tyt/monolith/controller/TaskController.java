package com.zjgsu.tyt.monolith.controller;

import com.zjgsu.tyt.monolith.entity.Task;
import com.zjgsu.tyt.monolith.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest request) {
        try {
            Task task = taskService.createTask(
                    request.title(),
                    request.description(),
                    request.reward(),
                    request.publisherId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<Task> getTasks() {
        return taskService.getTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getTaskById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<?> claimTask(
            @PathVariable Long id,
            @RequestBody ClaimTaskRequest request) {
        try {
            Task task = taskService.claimTask(id, request.runnerId());
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            HttpStatus status = e.getMessage().contains("不存在")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public record CreateTaskRequest(
            String title,
            String description,
            BigDecimal reward,
            Long publisherId) {
    }

    public record ClaimTaskRequest(Long runnerId) {
    }
}
