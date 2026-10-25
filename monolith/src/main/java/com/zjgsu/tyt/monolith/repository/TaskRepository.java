package com.zjgsu.tyt.monolith.repository;

import com.zjgsu.tyt.monolith.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Task.Status status);

    List<Task> findByPublisher_Id(Long publisherId);

    List<Task> findByRunner_Id(Long runnerId);
}
