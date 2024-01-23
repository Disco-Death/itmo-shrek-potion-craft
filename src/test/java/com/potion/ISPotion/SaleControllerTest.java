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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value=SaleController.class)
// @ContextConfiguration(classes={WebSecurityConfig.class, MvcConfig.class})
// @AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
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
    public void testSaleMethodWithAllowedRole() throws Exception {
        var user = new User(); // Создание пользователя

        var userRoles = new HashSet<Role>();
        userRoles.add(Role.SALES_DEPT);
        user.setRoles(userRoles); // Установка роли пользователя

        when(userRepository.findByUsername(any())).thenReturn(user); // Мокирование метода findById() репозитория пользователя

        Potion potion = new Potion();
        potion.setName("");
        Sale sale1 = new Sale(); // Создание объекта Sale
        sale1.setPotion(potion);
        Sale sale2 = new Sale(); // Создание объекта Sale
        sale2.setPotion(potion);

        when(saleRepository.findAll()).thenReturn(Arrays.asList(sale1, sale2)); // Мокирование метода findAll() репозитория продаж

        mockMvc.perform(get("/sale")
                        .with(user("user").roles("SALES_DEPT"))) // Выполнение GET запроса на /sale с пользователем, у которого есть роль SALES_DEPT
                .andExpect(status().isOk()) // Проверка статуса ответа
                .andExpect(view().name("sale")) // Проверка имени вьюхи
                .andExpect(model().attributeExists("title")) // Проверка наличия атрибута "title" в модели
                .andExpect(model().attribute("sales", hasSize(2))); // Проверка размера списка продаж в модели
    }

    @Test
    public void testSaleMethodWithNotAllowedRole() throws Exception {
        User user = new User(); // Создание пользователя

        var userRoles = new HashSet<Role>();
        userRoles.add(Role.TEST_DEPT);
        user.setRoles(userRoles); // Установка роли пользователя

        when(userRepository.findByUsername(any())).thenReturn(user); // Мокирование метода findById() репозитория пользователя

        mockMvc.perform(get("/sale")
                        .with(user("user").roles("CUSTOMER_SERVICE"))) // Выполнение GET запроса на /sale с пользователем, у которого есть роль CUSTOMER_SERVICE
                .andExpect(status().is3xxRedirection()) // Проверка, что ответ - редирект
                .andExpect(redirectedUrl("/home")); // Проверка URL, на который происходит редирект
    }
}
