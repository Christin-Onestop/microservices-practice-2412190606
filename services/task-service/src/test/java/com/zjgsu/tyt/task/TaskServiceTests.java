package com.zjgsu.tyt.task;

import com.zjgsu.tyt.task.client.UserServiceClient;
import com.zjgsu.tyt.task.entity.Task;
import com.zjgsu.tyt.task.repository.TaskRepository;
import com.zjgsu.tyt.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTests {

    private TaskRepository taskRepository;
    private UserServiceClient userServiceClient;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        userServiceClient = mock(UserServiceClient.class);
        taskService = new TaskService(taskRepository, userServiceClient);
    }

    @Test
    void createTaskShouldValidatePublisherBeforeSaving() {
        Task savedTask = new Task(
                "代取快递", "到驿站取件", new BigDecimal("5.00"), 1L);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(
                "代取快递", "到驿站取件", new BigDecimal("5.00"), 1L);

        verify(userServiceClient).validateUser(1L);
        verify(taskRepository).save(any(Task.class));
        assertEquals("代取快递", result.getTitle());
    }

    @Test
    void invalidRewardShouldNotCallUserServiceOrSave() {
        assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask(
                        "代取快递", "到驿站取件", BigDecimal.ZERO, 1L));

        verifyNoInteractions(userServiceClient);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void missingTaskShouldThrowException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> taskService.findById(999L));
    }
}