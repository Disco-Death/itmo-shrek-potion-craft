package com.potion.ISPotion;

import com.potion.ISPotion.Classes.Ingredient;
import com.potion.ISPotion.Classes.Potion;
import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.Controllers.IngredientController;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
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

@WebMvcTest(value=IngredientController.class)
public class IngredientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IngredientRepository ingredientRepository;
    @MockBean
    private UserRepository userRepository;

    @Test
    public void testIngredientWithAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.PICKING_DEPT);
        user.setRoles(userRoles);

        Ingredient ingredient1 = new Ingredient();
        Ingredient ingredient2 = new Ingredient();

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(ingredientRepository.findAll()).thenReturn(Arrays.asList(ingredient1, ingredient2));

        mockMvc.perform(get("/ingredient")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk())
                .andExpect(view().name("ingredient"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("ingredients", hasSize(2)));
    }

    @Test
    public void testIngredientAdd() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.HEAD);
        user.setRoles(userRoles);

        var ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("test name");
        ingredient.setProperty("test property");

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        mockMvc.perform(post("/ingredient/add")
                        .param("name", ingredient.getName())
                        .param("property", ingredient.getProperty())
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ingredient"))
                .andExpect(view().name("redirect:/ingredient"));

        var ingredientCaptor = ArgumentCaptor.forClass(Ingredient.class);
        verify(ingredientRepository).save(ingredientCaptor.capture());

        var capturedPotion = ingredientCaptor.getValue();
        assertEquals(ingredient.getName(), capturedPotion.getName());
        assertEquals(ingredient.getProperty(), capturedPotion.getProperty());
    }
}
