package com.potion.ISPotion;

import com.potion.ISPotion.Classes.Potion;
import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.Controllers.ReportController;
import com.potion.ISPotion.repo.ReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(value= ReportController.class)
public class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportRepository reportRepository;

    @Test
    public void testReportWithAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.HEAD);
        user.setRoles(userRoles);

        Report report1 = new Report();
        Report report2 = new Report();

        when(reportRepository.findAll()).thenReturn(Arrays.asList(report1, report2));

        mockMvc.perform(get("/report")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk())
                .andExpect(view().name("report"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("reports", hasSize(2)));
    }
}
