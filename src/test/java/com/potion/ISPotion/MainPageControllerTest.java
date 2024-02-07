package com.potion.ISPotion;

import com.potion.ISPotion.Controllers.MainPageController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value=MainPageController.class)
public class MainPageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHomeMethod() throws Exception {
        mockMvc.perform(get("/")
                        .with(user("username")))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("title"));
    }
}
