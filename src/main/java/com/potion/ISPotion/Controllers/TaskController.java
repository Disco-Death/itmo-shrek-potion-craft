package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.Task;
import com.potion.ISPotion.Classes.TaskStatus;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.TaskRepository;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import com.potion.ISPotion.utils.TaskService;
import com.potion.ISPotion.utils.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("permissionsParts")
    public Set<String> headerPermission(@CurrentSecurityContext(expression="authentication")
                                        Authentication authentication) {
        return AuthUtils.getHeaderPermissions(userRepository, authentication);
    }

    @GetMapping
    public String tasks(@CurrentSecurityContext(expression = "authentication")
                            Authentication authentication,
                        Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN,
                Role.EMPLOYEE
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        var user = AuthUtils.getUserByAuthentication(userRepository, authentication);

        var tasks = taskService.getAllTasksByReviewerIdAndExecutorId(user.getId());
        model.addAttribute("tasks", tasks);
        model.addAttribute("title", "Задачи");

        return "task";
    }

    @GetMapping("/all")
    public String getAllTasks(@CurrentSecurityContext(expression = "authentication")
                                  Authentication authentication,
                              Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        var tasks = taskRepository.findAll();
        model.addAttribute("tasks", tasks);

        return "task";
    }

    @GetMapping("/{userId}")
    public String getTasksByUserId(@CurrentSecurityContext(expression = "authentication")
                                       Authentication authentication,
                                   @PathVariable Long userId,
                                   Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN,
                Role.EMPLOYEE
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        List<Task> tasks = taskRepository.findAllByExecutorId(userId);
        model.addAttribute("tasks", tasks);
        return "task";
    }

    @GetMapping("/review")
    public String getTaskInReview(@CurrentSecurityContext(expression = "authentication")
                                      Authentication authentication,
                                  Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        var user = AuthUtils.getUserByAuthentication(userRepository, authentication);

        var tasks = taskService.getTaskByReviewerIdOnReview(user.getId());
        model.addAttribute("tasks", tasks);

        return "task-review";
    }

    @GetMapping("/new")
    public String createTaskDisplay(@CurrentSecurityContext(expression = "authentication")
                                        Authentication authentication,
                                    Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        var executors = userService.findAllExecutors();

        model.addAttribute("title", "Добавить задачу");
        model.addAttribute("executors", executors);

        return "task-add";
    }

    @PostMapping("/new")
    public String createTask(@CurrentSecurityContext(expression = "authentication")
                                 Authentication authentication,
                             @RequestParam Long executorId,
                             @RequestParam String description,
                             @RequestParam String deadline,
                             Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Task task = new Task();
        task.setDescription(description);

        // Установка крайнего срока
        LocalDateTime deadlineDateTime = LocalDateTime.parse(deadline, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        task.setDeadline(deadlineDateTime);

        if (!userRepository.existsById(executorId))
            return "redirect:/tasks";

        User executor = userRepository.findById(executorId).orElseThrow();

        var user = AuthUtils.getUserByAuthentication(userRepository, authentication);

        task.setExecutor(executor);
        task.setReviewer(user);
        task.setStatus(TaskStatus.ASSIGNED);
        task.setCreatedAt(LocalDateTime.now()); // Установка времени создания задания
        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @PostMapping("/{taskId}")
    public String deleteTaskPost(@CurrentSecurityContext(expression = "authentication")
                                 Authentication authentication,
                                 @PathVariable Long taskId,
                                 Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        taskRepository.deleteById(taskId);
        return "redirect:/tasks";
    }

    @PostMapping("/{taskId}/changeStatus")
    public String changeStatus(@CurrentSecurityContext(expression = "authentication")
                                   Authentication authentication,
                               @PathVariable Long taskId,
                               @RequestParam String newStatus,
                               Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN,
                Role.EMPLOYEE
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Задание не найдено"));

        if ("STARTED".equals(newStatus) || "SENT_FOR_REVIEW".equals(newStatus)) {
            task.setStatus(TaskStatus.valueOf(newStatus));
        } else {
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
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }
}
