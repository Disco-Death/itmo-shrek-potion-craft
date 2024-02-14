package com.potion.ISPotion;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.Controllers.TaskController;
import com.potion.ISPotion.repo.TaskRepository;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.TaskService;
import com.potion.ISPotion.utils.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value= TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TaskService taskService;
    @MockBean
    private UserService userService;

    @Test
    public void testTaskWithAllowedRole() throws Exception {
        var user = new User(); // Создание пользователя
        user.setId(1L);
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.EMPLOYEE);
        user.setRoles(userRoles); // Установка роли пользователя

        var task1 = new Task();
        task1.setReviewer(user);
        task1.setExecutor(user);
        task1.setDirectorTask(true);
        task1.setDescription("task 1");
        task1.setStatus(TaskStatus.STARTED);
        var task2 = new Task();
        task2.setReviewer(user);
        task2.setExecutor(user);
        task2.setDirectorTask(true);
        task2.setDescription("task 2");
        task2.setStatus(TaskStatus.REVIEW_STARTED);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(taskService.getAllTasksByReviewerIdAndExecutorId(anyLong())).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk()) // Проверка статуса ответа
                .andExpect(view().name("task")) // Проверка имени вьюхи
                .andExpect(model().attributeExists("title")) // Проверка наличия атрибута "title" в модели
                .andExpect(model().attribute("tasks", hasSize(2))); // Проверка размера списка продаж в модели
    }

    @Test
    public void testTaskAdd() throws Exception {
        var user = new User();
        user.setId(1L);
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.HEAD);
        user.setRoles(userRoles);

        var task = new Task();
        task.setReviewer(user);
        task.setExecutor(user);
        task.setDirectorTask(true);
        task.setDescription("task 1");
        task.setDeadline(LocalDateTime.of(1, 1, 1, 1, 1));

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/tasks/new")
                        .param("executorId", task.getExecutor().getId().toString())
                        .param("description", task.getDescription())
                        .param("deadline", task.getDeadline().toString())
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(view().name("redirect:/tasks"));

        var taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        var capturedTask = taskCaptor.getValue();
        assertEquals(task.getDescription(), capturedTask.getDescription());
        assertEquals(task.getExecutor(), capturedTask.getExecutor());
        assertEquals(task.getReviewer(), capturedTask.getReviewer());
        assertEquals(task.getDeadline(), capturedTask.getDeadline());
        assertEquals(TaskStatus.ASSIGNED, capturedTask.getStatus());
    }

    @Test
    public void testTaskEdit() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.HEAD);
        user.setRoles(userRoles);

        var task = new Task();
        task.setReviewer(user);
        task.setExecutor(user);
        task.setDirectorTask(true);
        task.setDescription("task 1");
        task.setStatus(TaskStatus.STARTED);

        var newTaskStatus = TaskStatus.ACCEPT_COMPLETED;

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        mockMvc.perform(post("/tasks/1/changeStatus")
                        .param("newStatus", newTaskStatus.toString())
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(view().name("redirect:/tasks"));

        var taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        var capturedTask = taskCaptor.getValue();
        assertEquals(task.getStatus(), capturedTask.getStatus());
    }

    @Test
    public void testTaskDelete() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.HEAD);
        user.setRoles(userRoles);

        var task = new Task();
        task.setId(1L);

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(post("/tasks/1")
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(view().name("redirect:/tasks"));

        var taskIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(taskRepository).deleteById(taskIdCaptor.capture());

        long capturedTaskId = taskIdCaptor.getValue();
        assertEquals(task.getId(), capturedTaskId);
    }
}
