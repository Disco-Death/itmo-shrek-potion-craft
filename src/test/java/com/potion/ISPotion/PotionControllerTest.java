package com.potion.ISPotion;

import com.potion.ISPotion.Classes.Potion;
import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.Controllers.PotionController;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
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

        Potion potion1 = new Potion();
        Potion potion2 = new Potion();

        when(potionRepository.findAll()).thenReturn(Arrays.asList(potion1, potion2));

        mockMvc.perform(get("/potion")
                        .with(user(user.getUsername()).roles(user.getRoles().toString())))
                .andExpect(status().isOk())
                .andExpect(view().name("potion"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("potions", hasSize(2)));
    }
}