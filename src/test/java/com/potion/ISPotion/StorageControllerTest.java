package com.potion.ISPotion;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.Controllers.StorageController;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.StorageCellRepository;
import com.potion.ISPotion.repo.StorageRecordRepository;
import com.potion.ISPotion.utils.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(value= StorageController.class)
public class StorageControllerTest {
    @Autowired
    private MockMvc mockMvc;

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
    public void testStorageWithAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.EMPLOYEE);
        user.setRoles(userRoles);

        StorageCell cell1 = new StorageCell();
        cell1.setEntity(StorageEntity.Potion);
        StorageCell cell2 = new StorageCell();
        cell2.setEntity(StorageEntity.Ingredient);

        StorageRecord record1 = new StorageRecord();
        record1.setOperation(StorageRecordOperation.ADD);
        record1.setDate_add(Instant.MIN);
        record1.setDate_upd(Instant.MIN);
        StorageRecord record2 = new StorageRecord();
        record2.setOperation(StorageRecordOperation.ADD);
        record2.setDate_add(Instant.MIN);
        record2.setDate_upd(Instant.MIN);

        when(storageCellRepository.findAll()).thenReturn(Arrays.asList(cell1, cell2));
        when(storageRecordRepository.findAll()).thenReturn(Arrays.asList(record1, record2));

        mockMvc.perform(get("/storage")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk())
                .andExpect(view().name("storage"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("cells", hasSize(2)))
                .andExpect(model().attribute("records", hasSize(2)));
    }
}