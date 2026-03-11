package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository; 

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void addTask(String title, String description) {
        Task task = new Task(title, description);
        taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public void toggleTask(Long id) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setCompleted(!task.isCompleted());
            // Сохраняем изменения
            taskRepository.save(task);
        });
    }
}