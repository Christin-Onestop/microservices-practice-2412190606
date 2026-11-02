package com.zjgsu.tyt.task.repository;

import com.zjgsu.tyt.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}