package com.potion.ISPotion;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.Controllers.RegistrationController;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.WebCamService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value=RegistrationController.class)
public class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    WebCamService webCamService;

    @Test
    public void testAuthenticationProcess() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.EMPLOYEE);
        user.setRoles(userRoles);

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(get("/home")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    public void testRegistrationProcess() throws Exception {
        var user = new User();
        user.setRoles(Collections.singleton(Role.ADMIN));
        user.setUsername("Ivan");

        when(userRepository.findByUsername(anyString())).thenReturn(null);

        mockMvc.perform(post("/registration")
                        .param("username", user.getUsername())
                        .with(user(user.getUsername()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(view().name("redirect:/login"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals(user.getUsername(), capturedUser.getUsername());
        assertTrue(capturedUser.isActive());
    }
}
