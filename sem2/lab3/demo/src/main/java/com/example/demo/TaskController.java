package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    private final TaskService taskService;  // Используем final

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Главная страница
    @GetMapping("/")
    public String showTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "index";
    }

    // Добавить задачу
    @PostMapping("/add")
    public String addTask(@RequestParam String title,
                          @RequestParam String description) {
        taskService.addTask(title, description);
        return "redirect:/";
    }

    // Удалить задачу - только для MODERATOR
    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/";
    }

    // Изменить статус задачи - только для MODERATOR
    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/toggle/{id}")
    public String toggleTask(@PathVariable Long id) {
        taskService.toggleTask(id);
        return "redirect:/";
    }

    // Страница логина
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}