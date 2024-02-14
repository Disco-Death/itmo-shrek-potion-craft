package com.potion.ISPotion;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.Controllers.StorageController;
import com.potion.ISPotion.repo.*;
import com.potion.ISPotion.utils.StorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
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
    @MockBean
    private UserRepository userRepository;

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
        record1.setDateAdd(Instant.EPOCH);
        record1.setDateUpd(Instant.EPOCH);
        StorageRecord record2 = new StorageRecord();
        record2.setOperation(StorageRecordOperation.SUBTRACTION);
        record2.setDateAdd(Instant.EPOCH);
        record2.setDateUpd(Instant.EPOCH);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
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

    @Test
    public void testStorageAddIngredient() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.DIRECTOR);
        user.setRoles(userRoles);

        var storageCell = new StorageCell();
        storageCell.setEntity(StorageEntity.Ingredient);
        storageCell.setEntity_id(1L);
        storageCell.setQuantity(100L);

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(post("/storage/add")
                        .param("entity", "Ingredient")
                        .param("ingredientId", storageCell.getEntity_id().toString())
                        .param("potionId", "")
                        .param("quantity", storageCell.getQuantity().toString())
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/storage"))
                .andExpect(view().name("redirect:/storage"));

        var storageCellCaptor = ArgumentCaptor.forClass(StorageCell.class);
        verify(storageService).storageCellCreate(storageCellCaptor.capture());

        var capturedStorageCell = storageCellCaptor.getValue();
        assertEquals(storageCell.getQuantity(), capturedStorageCell.getQuantity());
        assertEquals(storageCell.getEntity(), capturedStorageCell.getEntity());
        assertEquals(storageCell.getEntity_id(), capturedStorageCell.getEntity_id());
        assertEquals(0, capturedStorageCell.getTestApproved());
    }

    @Test
    public void testStorageAddPotion() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.DIRECTOR);
        user.setRoles(userRoles);

        var storageCell = new StorageCell();
        storageCell.setEntity(StorageEntity.Potion);
        storageCell.setEntity_id(1L);
        storageCell.setQuantity(100L);

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(post("/storage/add")
                        .param("entity", "Potion")
                        .param("potionId", storageCell.getEntity_id().toString())
                        .param("ingredientId", "")
                        .param("quantity", storageCell.getQuantity().toString())
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/storage"))
                .andExpect(view().name("redirect:/storage"));

        var storageCellCaptor = ArgumentCaptor.forClass(StorageCell.class);
        verify(storageService).storageCellCreate(storageCellCaptor.capture());

        var capturedStorageCell = storageCellCaptor.getValue();
        assertEquals(storageCell.getQuantity(), capturedStorageCell.getQuantity());
        assertEquals(storageCell.getEntity(), capturedStorageCell.getEntity());
        assertEquals(storageCell.getEntity_id(), capturedStorageCell.getEntity_id());
        assertEquals(0, capturedStorageCell.getTestApproved());
    }

    @Test
    public void testStorageEdit() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.DIRECTOR);
        user.setRoles(userRoles);

        var storageCell = new StorageCell();
        storageCell.setId(1L);
        storageCell.setQuantity(100L);

        var newStorageCellQuantity = 101L;

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(storageCellRepository.existsById(anyLong())).thenReturn(true);
        when(storageCellRepository.findById(anyLong())).thenReturn(Optional.of(storageCell));

        mockMvc.perform(post("/storage/edit/1")
                        .param("quantity", Long.toString(newStorageCellQuantity))
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/storage"))
                .andExpect(view().name("redirect:/storage"));

        var storageCellCaptor = ArgumentCaptor.forClass(StorageCell.class);
        var storageCellQuantityCaptor = ArgumentCaptor.forClass(Long.class);
        verify(storageService).storageCellUpdate(storageCellCaptor.capture(), storageCellQuantityCaptor.capture());

        var capturedStorageCellQuantuty = storageCellQuantityCaptor.getValue();
        assertEquals(newStorageCellQuantity, capturedStorageCellQuantuty);
    }

    @Test
    public void testStorageDelete() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.DIRECTOR);
        user.setRoles(userRoles);

        var storageCell = new StorageCell();
        storageCell.setId(1L);
        storageCell.setEntity(StorageEntity.Potion);
        storageCell.setQuantity(100L);
        storageCell.setEntity_id(2L);
        storageCell.setTestApproved(1);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(storageCellRepository.existsById(anyLong())).thenReturn(true);
        when(storageCellRepository.findById(anyLong())).thenReturn(Optional.of(storageCell));

        mockMvc.perform(post("/storage/delete/1")
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/storage"))
                .andExpect(view().name("redirect:/storage"));

        var storageCellCaptor = ArgumentCaptor.forClass(StorageCell.class);
        verify(storageService).storageCellRemove(storageCellCaptor.capture());

        var capturedStorageCell = storageCellCaptor.getValue();
        assertEquals(storageCell.getId(), capturedStorageCell.getId());
        assertEquals(storageCell.getQuantity(), capturedStorageCell.getQuantity());
        assertEquals(storageCell.getEntity_id(), capturedStorageCell.getEntity_id());
        assertEquals(storageCell.getEntity(), capturedStorageCell.getEntity());
        assertEquals(storageCell.getTestApproved(), capturedStorageCell.getTestApproved());
    }
}
