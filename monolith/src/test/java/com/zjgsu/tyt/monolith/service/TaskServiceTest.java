package com.zjgsu.tyt.monolith.service;

import com.zjgsu.tyt.monolith.entity.Task;
import com.zjgsu.tyt.monolith.entity.User;
import com.zjgsu.tyt.monolith.repository.TaskRepository;
import com.zjgsu.tyt.monolith.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask(
                        " ", "description", new BigDecimal("5.00"), 1L));
        verifyNoInteractions(userRepository, taskRepository);
    }

    @Test
    void shouldRejectNonPositiveReward() {
        assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask(
                        "取快递", "description", BigDecimal.ZERO, 1L));
        verifyNoInteractions(userRepository, taskRepository);
    }

    @Test
    void shouldRejectClaimingOwnTask() {
        User alice = new User("alice", "STUDENT");
        alice.setId(1L);

        Task task = new Task(
                "取快递", "description", new BigDecimal("5.00"), alice);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));

        assertThrows(IllegalArgumentException.class,
                () -> taskService.claimTask(10L, 1L));

        verify(taskRepository, never()).save(any(Task.class));
    }
}