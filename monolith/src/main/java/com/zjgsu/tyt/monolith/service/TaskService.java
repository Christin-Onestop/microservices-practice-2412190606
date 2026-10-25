package com.zjgsu.tyt.monolith.service;

import com.zjgsu.tyt.monolith.entity.Task;
import com.zjgsu.tyt.monolith.entity.User;
import com.zjgsu.tyt.monolith.repository.TaskRepository;
import com.zjgsu.tyt.monolith.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
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

        User publisher = userRepository.findById(publisherId)
                .orElseThrow(() ->
                        new IllegalArgumentException("发布者不存在"));

        Task task = new Task(title.trim(), description, reward, publisher);
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("任务不存在"));
    }

    @Transactional
    public Task claimTask(Long taskId, Long runnerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new IllegalArgumentException("任务不存在"));

        User runner = userRepository.findById(runnerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("接单用户不存在"));

        if (task.getStatus() != Task.Status.OPEN) {
            throw new IllegalArgumentException("该任务已被接单，无法重复接单");
        }

        if (task.getPublisher().getId().equals(runner.getId())) {
            throw new IllegalArgumentException("不能接取自己发布的任务");
        }

        task.setRunner(runner);
        task.setStatus(Task.Status.CLAIMED);

        return taskRepository.save(task);
    }
}
