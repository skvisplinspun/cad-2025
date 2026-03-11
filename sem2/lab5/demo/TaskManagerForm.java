import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TaskManagerForm extends JFrame {
    private JList<String> taskList;
    private DefaultListModel<String> taskListModel;
    private String authHeader;

    public TaskManagerForm(String authHeader) {
        this.authHeader = authHeader;
        setTitle("Task Manager");
        setSize(500, 400); // Увеличил размер
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 14)); // Увеличил шрифт
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Обновить задачи");
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadTasks());

        // Загружаем задачи при запуске
        loadTasks();
    }

    private void loadTasks() {
        try {
            URL url = new URL("http://localhost:8080/api");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + authHeader);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    jsonResponse.append(inputLine);
                }
                in.close();

                System.out.println("Полученный JSON: " + jsonResponse.toString());

                if (jsonResponse.toString().contains("<!DOCTYPE html>")) {
                    taskListModel.clear();
                    taskListModel.addElement("Ошибка авторизации!");
                    return;
                }

                parseJson(jsonResponse.toString());
            } else if (responseCode == 401) {
                taskListModel.clear();
                taskListModel.addElement("Ошибка: неправильный логин/пароль");
            } else {
                taskListModel.clear();
                taskListModel.addElement("Ошибка сервера: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            taskListModel.clear();
            taskListModel.addElement("Ошибка соединения: " + e.getMessage());
        }
    }

    private void parseJson(String json) {
        try {
            taskListModel.clear();

            json = json.trim();

            if (!json.startsWith("[") || !json.endsWith("]")) {
                taskListModel.addElement("Некорректный JSON формат");
                return;
            }

            String content = json.substring(1, json.length() - 1).trim();

            if (content.isEmpty()) {
                taskListModel.addElement("Нет задач");
                return;
            }

            String[] taskStrings = splitTasks(content);

            for (int i = 0; i < taskStrings.length; i++) {
                String taskJson = taskStrings[i].trim();

                // ИЗВЛЕКАЕМ ЗАГОЛОВОК
                String title = extractValue(taskJson, "title");
                String description = extractValue(taskJson, "description");
                String completed = extractValue(taskJson, "completed");
                String id = extractValue(taskJson, "id");

                if (title != null && !title.isEmpty()) {
                    // ИСПОЛЬЗУЕМ ЗАГОЛОВОК
                    String status = "true".equals(completed) ? "✓ Выполнено" : "⏳ В ожидании";
                    String displayText;

                    if (description != null && !description.isEmpty()) {
                        displayText = (i + 1) + ". " + title + " - " + description + " (" + status + ")";
                    } else {
                        displayText = (i + 1) + ". " + title + " (" + status + ")";
                    }

                    taskListModel.addElement(displayText);
                }
                else if (description != null && !description.isEmpty()) {
                    // Если заголовка нет, используем описание
                    String status = "true".equals(completed) ? "✓ Выполнено" : "⏳ В ожидании";
                    String displayText = (i + 1) + ". " + description + " (" + status + ")";
                    taskListModel.addElement(displayText);
                }
            }

            if (taskListModel.isEmpty()) {
                taskListModel.addElement("Не удалось распознать задачи");
            }

        } catch (Exception e) {
            e.printStackTrace();
            taskListModel.clear();
            taskListModel.addElement("Ошибка парсинга: " + e.getMessage());
        }
    }

    private String[] splitTasks(String content) {
        java.util.List<String> tasks = new java.util.ArrayList<>();
        int braceCount = 0;
        StringBuilder currentTask = new StringBuilder();

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '{') {
                braceCount++;
                if (braceCount == 1) {
                    currentTask = new StringBuilder();
                }
            }

            if (braceCount > 0) {
                currentTask.append(c);
            }

            if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    tasks.add(currentTask.toString());
                    if (i + 1 < content.length() && content.charAt(i + 1) == ',') {
                        i++;
                    }
                }
            }
        }

        return tasks.toArray(new String[0]);
    }

    private String extractValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int keyIndex = json.indexOf(searchKey);

            if (keyIndex == -1) {
                return null;
            }

            int valueStart = keyIndex + searchKey.length();
            int valueEnd = json.indexOf(',', valueStart);
            if (valueEnd == -1) {
                valueEnd = json.indexOf('}', valueStart);
            }
            if (valueEnd == -1) {
                valueEnd = json.length();
            }

            String value = json.substring(valueStart, valueEnd).trim();

            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }

            return value;
        } catch (Exception e) {
            return null;
        }
    }
}