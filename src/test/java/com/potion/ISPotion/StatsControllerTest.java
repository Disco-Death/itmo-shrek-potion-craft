package com.potion.ISPotion;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.Stat;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.Controllers.StatsController;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.StatsComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(value= StatsController.class)
public class StatsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private StatsComponent statsComponent;

    @Test
    public void testStatsWithAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.MERLIN);
        user.setRoles(userRoles);

        var stat1 = new Stat("O(n) [линейная]", "0.14");
        var stat2 = new Stat("O(n^2) [квадратичная]", "0.78");

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(statsComponent.getStats()).thenReturn(Arrays.asList(stat1, stat2));

        mockMvc.perform(get("/stats")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk())
                .andExpect(view().name("stats"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("stats", hasSize(2)));
    }

    @Test
    public void testStatsWithNotAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.EMPLOYEE);
        user.setRoles(userRoles);

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(get("/stats")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }
}
