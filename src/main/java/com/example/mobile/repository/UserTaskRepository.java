package com.example.mobile.repository;

import com.example.mobile.entity.TaskStatus;
import com.example.mobile.entity.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    List<UserTask> findAllByUserUsernameAndStatus(String username, TaskStatus status);
    Optional<UserTask> findByIdAndUserUsername(Long id, String username);
}
