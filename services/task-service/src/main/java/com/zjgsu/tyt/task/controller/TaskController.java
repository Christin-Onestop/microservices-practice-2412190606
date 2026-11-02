package com.zjgsu.tyt.task.controller;

import com.zjgsu.tyt.task.entity.Task;
import com.zjgsu.tyt.task.service.TaskService;
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

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            String description = (String) body.get("description");
            BigDecimal reward = new BigDecimal(body.get("reward").toString());
            Long publisherId = Long.valueOf(body.get("publisherId").toString());

            Task task = taskService.createTask(
                    title, description, reward, publisherId);

            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "请求参数错误：" + e.getMessage()));
        }
    }
}