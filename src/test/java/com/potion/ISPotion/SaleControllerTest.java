package com.potion.ISPotion;

import com.potion.ISPotion.Classes.Potion;
import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.Sale;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.Controllers.SaleController;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.SaleRepository;
import com.potion.ISPotion.repo.UserRepository;

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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value=SaleController.class)
public class SaleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleRepository saleRepository;

    @MockBean
    private PotionRepository potionRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private StorageService storageService;

    @Test
    public void testSaleWithAllowedRole() throws Exception {
        var user = new User(); // Создание пользователя
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.SALES_DEPT);
        user.setRoles(userRoles); // Установка роли пользователя

        Potion potion = new Potion();
        potion.setName("");
        Sale sale1 = new Sale(); // Создание объекта Sale
        sale1.setPotion(potion);
        Sale sale2 = new Sale(); // Создание объекта Sale
        sale2.setPotion(potion);

        when(userRepository.findByUsername(anyString())).thenReturn(user); // Мокирование метода findById() репозитория пользователя
        when(saleRepository.findAll()).thenReturn(Arrays.asList(sale1, sale2)); // Мокирование метода findAll() репозитория продаж

        mockMvc.perform(get("/sale")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk()) // Проверка статуса ответа
                .andExpect(view().name("sale")) // Проверка имени вьюхи
                .andExpect(model().attributeExists("title")) // Проверка наличия атрибута "title" в модели
                .andExpect(model().attribute("sales", hasSize(2))); // Проверка размера списка продаж в модели
    }

    @Test
    public void testSaleWithNotAllowedRole() throws Exception {
        User user = new User(); // Создание пользователя
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.TEST_DEPT);
        user.setRoles(userRoles); // Установка роли пользователя

        when(userRepository.findByUsername(anyString())).thenReturn(user); // Мокирование метода findById() репозитория пользователя

        mockMvc.perform(get("/sale")
                .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().is3xxRedirection()) // Проверка, что ответ - редирект
                .andExpect(redirectedUrl("/home")); // Проверка URL, на который происходит редирект
    }

    @Test
    public void testSaleAdd() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.HEAD);
        user.setRoles(userRoles);

        Potion potion = new Potion();
        potion.setId(1L);
        potion.setName("potion name");

        Sale sale = new Sale();
        sale.setPotion(potion);
        sale.setQuantity(5L);
        sale.setPrice(10L);
        sale.setClient("test client");

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(potionRepository.existsById(anyLong())).thenReturn(true);
        when(storageService.takePotionsFromStorageForSaleByPotionId(anyLong(), anyLong())).thenReturn(true);
        when(potionRepository.findById(anyLong())).thenReturn(Optional.of(potion));

        mockMvc.perform(post("/sale/add")
                        .param("potionId", potion.getId().toString())
                        .param("quantity", sale.getQuantity().toString())
                        .param("price", sale.getPrice().toString())
                        .param("client", sale.getClient())
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sale"))
                .andExpect(view().name("redirect:/sale"));

        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saleRepository).save(saleCaptor.capture());

        Sale capturedSale = saleCaptor.getValue();
        assertEquals(sale.getPotion(), capturedSale.getPotion());
        assertEquals(sale.getQuantity(), capturedSale.getQuantity());
        assertEquals(sale.getPrice(), capturedSale.getPrice());
        assertEquals(sale.getClient(), capturedSale.getClient());
    }

    @Test
    public void testSaleDelete() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.HEAD);
        user.setRoles(userRoles);

        Potion potion = new Potion();
        potion.setId(1L);
        potion.setName("potion name");

        Sale sale = new Sale();
        sale.setId(1L);
        sale.setPotion(potion);
        sale.setQuantity(5L);
        sale.setPrice(10L);
        sale.setClient("test client");

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(saleRepository.existsById(anyLong())).thenReturn(true);
        when(saleRepository.findById(anyLong())).thenReturn(Optional.of(sale));

        mockMvc.perform(post("/sale/delete/1")
                        .param("id", sale.getId().toString())
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sale"))
                .andExpect(view().name("redirect:/sale"));

        ArgumentCaptor<Long> saleIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(saleRepository).deleteById(saleIdCaptor.capture());

        long capturedSaleId = saleIdCaptor.getValue();
        assertEquals(sale.getId(), capturedSaleId);

        saleIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> saleQuantityCaptor = ArgumentCaptor.forClass(Long.class);
        verify(storageService).returnPotionsToStorageForSaleByPotionId(saleIdCaptor.capture(), saleQuantityCaptor.capture());

        capturedSaleId = saleIdCaptor.getValue();
        long capturedSaleQuantity = saleQuantityCaptor.getValue();
        assertEquals(sale.getId(), capturedSaleId);
        assertEquals(sale.getQuantity(), capturedSaleQuantity);
    }
}
