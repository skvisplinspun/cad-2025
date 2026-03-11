package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MessageController {

    private List<String> userMessages = new ArrayList<>();

    // 1. Основные CRUD методы
    @GetMapping("/")
    public String helloWorld() {
        return "Hello, World!";
    }

    @GetMapping("/messages")
    public List<String> getAllMessages() {
        return userMessages;
    }

    @PostMapping("/messages")
    public String publishMessage(@RequestBody String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Ошибка: сообщение не может быть пустым.";
        }
        userMessages.add(message);
        return "Message published successfully!";
    }

    @PutMapping("/messages/{index}")
    public String updateMessage(@PathVariable int index, @RequestBody String message) {
        if (index >= 0 && index < userMessages.size()) {
            userMessages.set(index, message);
            return "Message updated successfully!";
        }
        return "Message not found at index " + index;
    }

    @DeleteMapping("/messages/{index}")
    public String deleteMessage(@PathVariable int index) {
        if (index >= 0 && index < userMessages.size()) {
            userMessages.remove(index);
            return "Message deleted successfully!";
        }
        return "Message not found at index " + index;
    }

    // ========== ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ==========

    // 1. Очистка всех сообщений
    @DeleteMapping("/messages/clear")
    public String clearAllMessages() {
        userMessages.clear();
        return "All messages cleared successfully!";
    }

    // 2. Проверка на пустое сообщение уже встроена в publishMessage()

    // 3. Получить сообщение по индексу
    @GetMapping("/messages/{index}")
    public String getMessageByIndex(@PathVariable int index) {
        if (index >= 0 && index < userMessages.size()) {
            return userMessages.get(index);
        }
        return "Message not found at index " + index;
    }

    // 4. Ограничение количества сообщений (максимум 10)
    private static final int MAX_MESSAGES = 10;

    @PostMapping("/messages/limited")
    public String publishMessageLimited(@RequestBody String message) {
        if (message == null || message.trim().isEmpty()) {
            return "Ошибка: сообщение не может быть пустым.";
        }
        if (userMessages.size() >= MAX_MESSAGES) {
            userMessages.remove(0); // удаляем самое старое
        }
        userMessages.add(message);
        return "Message published successfully! (Limited storage)";
    }

    // 5. Получить количество сообщений
    @GetMapping("/messages/count")
    public int countMessages() {
        return userMessages.size();
    }

    // 6. Фильтрация по времени (заглушка)
    // В реальном проекте здесь нужна модель сообщения с полем timestamp
    // @GetMapping("/messages/after")
    // public List<String> getMessagesAfter(@RequestParam String dateTime) { ... }
}