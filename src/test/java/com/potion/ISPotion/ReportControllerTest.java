package com.potion.ISPotion;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.Controllers.ReportController;
import com.potion.ISPotion.repo.ReportRepository;
import com.potion.ISPotion.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(value=ReportController.class)
public class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReportRepository reportRepository;
    @MockBean
    private UserRepository userRepository;

    @Test
    public void testReportWithAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.HEAD);
        user.setRoles(userRoles);

        Report report1 = new Report();
        report1.setUser(user);
        report1.setDateAdd(Instant.EPOCH);
        report1.setDateUpd(Instant.EPOCH);
        Report report2 = new Report();
        report2.setUser(user);
        report2.setDateAdd(Instant.EPOCH);
        report2.setDateUpd(Instant.EPOCH);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(reportRepository.findAllByUser(any())).thenReturn(new HashSet<>(Arrays.asList(report1, report2)));

        mockMvc.perform(get("/report")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk())
                .andExpect(view().name("report"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("reports", hasSize(2)));
    }

    @Test
    public void testReportWithNotAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.EMPLOYEE);
        user.setRoles(userRoles);

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(get("/report")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    public void testReportAdd() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.DIRECTOR);
        user.setRoles(userRoles);

        var report = new Report();
        report.setId(1L);
        report.setSended(true);
        report.setBody("test body");
        report.setTitle("test title");
        report.setSubject("test subject");
        report.setUser(user);

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(post("/report/add")
                        .param("title", report.getTitle())
                        .param("subject", report.getSubject())
                        .param("body", report.getBody())
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/report"))
                .andExpect(view().name("redirect:/report"));

        var reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());

        var capturedReport = reportCaptor.getValue();
        assertEquals(report.getBody(), capturedReport.getBody());
        assertEquals(report.getTitle(), capturedReport.getTitle());
        assertEquals(report.getSubject(), capturedReport.getSubject());
    }

    @Test
    public void testReportEdit() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.DIRECTOR);
        user.setRoles(userRoles);

        var report = new Report();
        report.setId(1L);
        report.setTitle("Old title");
        report.setSubject("Old subject");
        report.setBody("Old body");
        report.setUser(user);

        var newTitle = "New title";
        var newSubject = "New subject";
        var newBody = "New body";

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(reportRepository.existsById(anyLong())).thenReturn(true);
        when(reportRepository.findById(anyLong())).thenReturn(Optional.of(report));

        mockMvc.perform(post("/report/edit/1")
                        .param("title", newTitle)
                        .param("subject", newSubject)
                        .param("body", newBody)
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/report"))
                .andExpect(view().name("redirect:/report"));

        var reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());

        var capturedReport = reportCaptor.getValue();
        assertEquals(report.getId(), capturedReport.getId());
        assertEquals(newTitle, capturedReport.getTitle());
        assertEquals(newSubject, capturedReport.getSubject());
        assertEquals(newBody, capturedReport.getBody());
    }

    @Test
    public void testReportDelete() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.DIRECTOR);
        user.setRoles(userRoles);

        var report = new Report();
        report.setUser(user);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(reportRepository.existsById(anyLong())).thenReturn(true);
        when(reportRepository.findById(anyLong())).thenReturn(Optional.of(report));

        mockMvc.perform(post("/report/delete/1")
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/report"))
                .andExpect(view().name("redirect:/report"));

        var reportIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(reportRepository).deleteById(reportIdCaptor.capture());

        var capturedReportId = reportIdCaptor.getValue();
        assertEquals(1, capturedReportId);
    }
}
