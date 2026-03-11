package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Главная страница
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        model.addAttribute("totalTasks", taskService.getTotalTaskCount());
        model.addAttribute("completedTasks", taskService.getCompletedTaskCount());
        return "index";
    }

    // Добавить задачу
    @PostMapping("/add")
    public String addTask(@RequestParam String title,
                          @RequestParam String description) {
        taskService.createTask(title, description);
        return "redirect:/";
    }

    // Удалить задачу
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/";
    }

    // Переключить статус задачи
    @GetMapping("/toggle/{id}")
    public String toggleTask(@PathVariable Long id) {
        taskService.toggleTaskCompletion(id);
        return "redirect:/";
    }
}