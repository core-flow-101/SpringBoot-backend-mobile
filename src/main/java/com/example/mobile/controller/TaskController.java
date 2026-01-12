package com.example.mobile.controller;

import com.example.mobile.dto.CreateTaskRequest;
import com.example.mobile.dto.TaskDto;
import com.example.mobile.dto.TaskTemplateDto;
import com.example.mobile.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/current")
    public ResponseEntity<List<TaskDto>> current() {
        return ResponseEntity.ok(taskService.getCurrentTasks());
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<TaskDto> complete(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.completeTask(taskId));
    }

    @PostMapping("/{taskId}/cancel")
    public ResponseEntity<TaskDto> cancel(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.cancelTask(taskId));
    }

    @GetMapping("/templates")
    public ResponseEntity<List<TaskTemplateDto>> templates() {
        return ResponseEntity.ok(taskService.getTemplates());
    }

    @PostMapping("/templates/{templateId}/take")
    public ResponseEntity<TaskDto> take(@PathVariable Long templateId) {
        return ResponseEntity.ok(taskService.takeTemplate(templateId));
    }

    @PostMapping("/custom")
    public ResponseEntity<TaskDto> createCustom(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.ok(taskService.createCustomTask(request));
    }
}
