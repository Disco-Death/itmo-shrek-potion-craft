package com.potion.ISPotion;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.Controllers.TestsController;
import com.potion.ISPotion.repo.*;
import com.potion.ISPotion.utils.StorageService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(value = TestsController.class)
public class TestsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PotionRepository potionRepository;
    @MockBean
    private IngredientRepository ingredientRepository;
    @MockBean
    private StorageCellRepository storageCellRepository;
    @MockBean
    private StorageRecordRepository storageRecordRepository;
    @MockBean
    private StorageService storageService;

    @Test
    public void testTestsWithAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.TEST_DEPT);
        user.setRoles(userRoles);

        var storageCell1 = new StorageCell();
        storageCell1.setEntity(StorageEntity.Potion);
        var storageCell2 = new StorageCell();
        storageCell2.setEntity(StorageEntity.Ingredient);
        var storageRecord1 = new StorageRecord();
        var storageRecord2 = new StorageRecord();

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(storageCellRepository.findAll()).thenReturn(Arrays.asList(storageCell1, storageCell2));
        when(storageRecordRepository.findAll()).thenReturn(Arrays.asList(storageRecord1, storageRecord2));

        mockMvc.perform(get("/tests")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk())
                .andExpect(view().name("tests"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("cells", hasSize(2)))
                .andExpect(model().attribute("records", hasSize(2)));
    }

    @Test
    public void testTestsWithNotAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.EMPLOYEE);
        user.setRoles(userRoles);

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(get("/tests")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    public void testTestsEdit() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.DIRECTOR);
        user.setRoles(userRoles);

        var storageCell1 = new StorageCell();
        storageCell1.setEntity(StorageEntity.Potion);
        storageCell1.setTestApproved(1);
        storageCell1.setId(1L);
        var storageCell2 = new StorageCell();
        storageCell2.setEntity(StorageEntity.Ingredient);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(storageCellRepository.existsById(anyLong())).thenReturn(true);
        when(storageCellRepository.findAll()).thenReturn(Arrays.asList(storageCell1, storageCell2));
        when(storageCellRepository.findById(anyLong())).thenReturn(Optional.of(storageCell1));

        mockMvc.perform(post("/tests/edit/1")
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tests"))
                .andExpect(view().name("redirect:/tests"));

        var storageCellCaptor = ArgumentCaptor.forClass(StorageCell.class);
        verify(storageCellRepository).save(storageCellCaptor.capture());

        var capturedStorageCellCaptor = storageCellCaptor.getValue();
        assertEquals(storageCell1.getId(), capturedStorageCellCaptor.getId());
        assertEquals(0, capturedStorageCellCaptor.getTestApproved());
    }
}
