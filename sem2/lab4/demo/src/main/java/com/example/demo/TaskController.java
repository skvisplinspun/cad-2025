package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/addTask")
    public String addTask(@RequestParam("title") String title,
                          @RequestParam("description") String description) {
        taskService.addTask(title, description);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/deleteTask/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping("/updateTask/{id}")
    public String updateTask(@PathVariable Long id) {
        taskService.toggleTask(id);
        return "redirect:/";
    }
}