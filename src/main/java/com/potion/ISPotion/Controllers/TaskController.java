package com.potion.ISPotion.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.potion.ISPotion.Classes.Task;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.TaskRepository;
import com.potion.ISPotion.repo.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getAllTasks(Model model) {
        List<Task> tasks = taskRepository.findAll();
        model.addAttribute("tasks", tasks);
        return "task";
    }

    @GetMapping("/{userId}")
    public String getTasksByUserId(@PathVariable Long userId, Model model) {
        List<Task> tasks = taskRepository.findByUserId(userId);
        model.addAttribute("tasks", tasks);
        return "task";
    }

    @PostMapping("/new")
    public String createTask(@RequestParam String description, @RequestParam String username) {
        Task task = new Task();
        task.setDescription(description);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Пользователь с именем " + username + " не найден");
        }

        task.setUser(user);
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @PostMapping("/{taskId}")
    public String deleteTaskPost(@PathVariable Long taskId) {
        taskRepository.deleteById(taskId);
        return "redirect:/tasks";
    }
}
