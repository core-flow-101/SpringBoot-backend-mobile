package com.example.mobile.service;

import com.example.mobile.dto.CreateTaskRequest;
import com.example.mobile.dto.TaskDto;
import com.example.mobile.dto.TaskTemplateDto;
import com.example.mobile.entity.*;
import com.example.mobile.repository.TaskTemplateRepository;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final UserRepository userRepository;
    private final TaskTemplateRepository taskTemplateRepository;
    private final UserTaskRepository userTaskRepository;

    public List<TaskDto> getCurrentTasks() {
        String username = getCurrentUsername();
        return userTaskRepository.findAllByUserUsernameAndStatus(username, TaskStatus.IN_PROGRESS)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskTemplateDto> getTemplates() {
        return taskTemplateRepository.findAllByActiveTrue()
                .stream()
                .map(template -> TaskTemplateDto.builder()
                        .id(template.getId())
                        .title(template.getTitle())
                        .description(template.getDescription())
                        .points(template.getPoints())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDto takeTemplate(Long templateId) {
        User user = getCurrentUser();
        TaskTemplate template = taskTemplateRepository.findById(templateId)
                .filter(t -> Boolean.TRUE.equals(t.getActive()))
                .orElseThrow(() -> new RuntimeException("Шаблон задачи не найден или отключен"));

        UserTask task = new UserTask();
        task.setUser(user);
        task.setCreatedBy(user);
        task.setTitle(template.getTitle());
        task.setDescription(template.getDescription());
        task.setPoints(template.getPoints());
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setType(TaskType.FIXED);

        return toDto(userTaskRepository.save(task));
    }

    @Transactional
    public TaskDto createCustomTask(CreateTaskRequest request) {
        User user = getCurrentUser();

        UserTask task = new UserTask();
        task.setUser(user);
        task.setCreatedBy(user);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPoints(request.getPoints());
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setType(TaskType.CUSTOM);

        return toDto(userTaskRepository.save(task));
    }

    @Transactional
    public TaskDto completeTask(Long taskId) {
        User user = getCurrentUser();
        UserTask task = userTaskRepository.findByIdAndUserUsername(taskId, user.getUsername())
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));

        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new RuntimeException("Задачу нельзя завершить в текущем статусе");
        }

        task.setStatus(TaskStatus.DONE);
        task.setCompletedAt(LocalDateTime.now());

        user.setPoints(user.getPoints() + task.getPoints());

        userTaskRepository.save(task);
        userRepository.save(user);

        return toDto(task);
    }

    @Transactional
    public TaskDto cancelTask(Long taskId) {
        User user = getCurrentUser();
        UserTask task = userTaskRepository.findByIdAndUserUsername(taskId, user.getUsername())
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));

        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new RuntimeException("Задачу нельзя отменить в текущем статусе");
        }

        task.setStatus(TaskStatus.CANCELLED);
        userTaskRepository.save(task);

        return toDto(task);
    }

    private TaskDto toDto(UserTask task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .points(task.getPoints())
                .status(task.getStatus())
                .type(task.getType())
                .createdBy(task.getCreatedBy() != null ? task.getCreatedBy().getUsername() : null)
                .createdAt(task.getCreatedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}
