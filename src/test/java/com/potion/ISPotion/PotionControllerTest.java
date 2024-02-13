package com.potion.ISPotion;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.Controllers.PotionController;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Array;
import java.util.*;

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

@WebMvcTest(value=PotionController.class)
public class PotionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PotionRepository potionRepository;

    @MockBean
    private IngredientRepository ingredientRepository;

    @Test
    public void testPotionWithAllowedRole() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.POTIONS_MAKING_DEPT);
        user.setRoles(userRoles);

        var ingredient = new Ingredient();
        ingredient.setName("test ingredient name");

        var potion1 = new Potion();
        potion1.setName("test potion name 1");
        potion1.setIngredients(Collections.singletonList(ingredient));
        var potion2 = new Potion();
        potion2.setName("test potion name 2");
        potion2.setIngredients(Collections.singletonList(ingredient));

        when(potionRepository.findAll()).thenReturn(Arrays.asList(potion1, potion2));

        mockMvc.perform(get("/potion")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk())
                .andExpect(view().name("potion"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("potions", hasSize(2)));
    }

    @Test
    public void testPotionAdd() throws Exception {
        var user = new User();
        user.setUsername("Test username");
        var userRoles = new HashSet<Role>();
        userRoles.add(Role.HEAD);
        user.setRoles(userRoles);

        var ingredient = new Ingredient();
        ingredient.setId(1L);

        Potion potion = new Potion();
        potion.setId(1L);
        potion.setName("potion name");
        ArrayList<Long> ingredientsIds = new ArrayList<>();
        ingredientsIds.add(ingredient.getId());
        potion.setIngredientsIds(ingredientsIds);

        when(ingredientRepository.findById(anyLong())).thenReturn(Optional.of(ingredient));

        mockMvc.perform(post("/potion/add")
                        .param("name", potion.getId().toString())
                        .param("ingredientsIds", ingredient.getId().toString())
                        .with(user(user.getUsername()).roles(user.getRoles().toString()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/potion"))
                .andExpect(view().name("redirect:/potion"));

        var potionCaptor = ArgumentCaptor.forClass(Potion.class);
        verify(potionRepository).save(potionCaptor.capture());

        var capturedPotion = potionCaptor.getValue();
        assertEquals(potion.getIngredientsIds(), capturedPotion.getIngredientsIds());
    }
}
