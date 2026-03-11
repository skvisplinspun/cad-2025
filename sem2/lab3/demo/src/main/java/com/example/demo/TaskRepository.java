package com.example.demo;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {
    private final List<Task> tasks = new ArrayList<>();
    private Long idCounter = 1L;

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idCounter++);
        }
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
}