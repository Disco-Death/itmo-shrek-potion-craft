package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.Task;
import com.potion.ISPotion.Classes.TaskStatus;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.TaskRepository;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import com.potion.ISPotion.utils.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @GetMapping
    public String getAllTasks(Model model) {
        List<Task> tasks = taskRepository.findAll();
        model.addAttribute("tasks", tasks);

        List<Task> directorTasks = taskService.getDirectorTasks();
        model.addAttribute("directorTasks", directorTasks);

        return "task";
    }

    @GetMapping("/{userId}")
    public String getTasksByUserId(@PathVariable Long userId, Model model) {
        List<Task> tasks = taskRepository.findByUserId(userId);
        model.addAttribute("tasks", tasks);
        return "task";
    }

    @GetMapping("/director/tasks")
    public String getDirectorTasks(Model model) {
        List<Task> directorTasks = taskService.getDirectorTasks();
        model.addAttribute("directorTasks", directorTasks);
        return "director-tasks";
    }

    @PostMapping("/new")
    public String createTask(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                             @RequestParam String description, @RequestParam String username,
                             @RequestParam String deadline) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.HEAD,
                Role.MERLIN,
                Role.EMPLOYEE
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Task task = new Task();
        task.setDescription(description);

        // Установка крайнего срока
        LocalDateTime deadlineDateTime = LocalDateTime.parse(deadline, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        task.setDeadline(deadlineDateTime);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Пользователь с именем " + username + " не найден");
        }

        task.setUser(user);
        task.setStatus(TaskStatus.ASSIGNED);
        task.setCreatedAt(LocalDateTime.now()); // Установка времени создания задания
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @PostMapping("/{taskId}")
    public String deleteTaskPost(@CurrentSecurityContext(expression = "authentication")
                                 Authentication authentication,
                                 @PathVariable Long taskId) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.HEAD,
                Role.MERLIN,
                Role.EMPLOYEE
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        taskRepository.deleteById(taskId);
        return "redirect:/tasks";
    }

    @PostMapping("/{taskId}/changeStatus")
    public String changeStatus(@PathVariable Long taskId, @RequestParam String newStatus,
                               @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Задание не найдено"));

        if ("STARTED".equals(newStatus) || "SENT_FOR_REVIEW".equals(newStatus)) {
            task.setStatus(TaskStatus.valueOf(newStatus));
        } else {
            Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                    Role.HEAD,
                    Role.MERLIN,
                    Role.EMPLOYEE
            ));

            Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);

            if (AuthUtils.anyAllowedRole(userRoles, allowedRoles)) {
                switch (newStatus) {
                    case "ASSIGNED":
                        task.setStatus(TaskStatus.ASSIGNED);
                        break;
                    case "REVIEW_STARTED":
                        task.setStatus(TaskStatus.REVIEW_STARTED);
                        break;
                    case "SENT_FOR_REWORK":
                        task.setStatus(TaskStatus.SENT_FOR_REWORK);
                        break;
                    case "ACCEPT_COMPLETED":
                        task.setStatus(TaskStatus.ACCEPT_COMPLETED);
                        break;
                    default:
                        return "redirect:/tasks";
                }
            } else {
                return "redirect:/tasks";
            }
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

}
