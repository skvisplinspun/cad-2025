package com.example.demo;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {
    private List<Task> tasks = new ArrayList<>();

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public Task save(Task task) {
        tasks.add(task);
        return task;
    }

    public Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    public void deleteById(Long id) {
        tasks.removeIf(task -> task.getId().equals(id));
    }

    public void updateTask(Task task) {
        findById(task.getId()).ifPresent(existingTask -> {
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setCompleted(task.isCompleted());
        });
    }
}