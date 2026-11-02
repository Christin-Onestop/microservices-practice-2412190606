package com.zjgsu.tyt.task.service;

import com.zjgsu.tyt.task.client.UserServiceClient;
import com.zjgsu.tyt.task.entity.Task;
import com.zjgsu.tyt.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;

    public TaskService(
            TaskRepository taskRepository,
            UserServiceClient userServiceClient) {
        this.taskRepository = taskRepository;
        this.userServiceClient = userServiceClient;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("任务不存在，ID：" + id));
    }

    public Task createTask(
            String title,
            String description,
            BigDecimal reward,
            Long publisherId) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("任务标题不能为空");
        }

        if (reward == null || reward.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("任务赏金必须大于 0");
        }

        if (publisherId == null || publisherId <= 0) {
            throw new IllegalArgumentException("发布者 ID 必须大于 0");
        }

        // 先验证用户，再保存任务
        userServiceClient.validateUser(publisherId);

        Task task = new Task(title, description, reward, publisherId);
        return taskRepository.save(task);
    }
}