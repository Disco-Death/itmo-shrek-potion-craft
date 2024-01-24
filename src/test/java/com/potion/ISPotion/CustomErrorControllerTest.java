package com.potion.ISPotion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value=CustomErrorControllerTest.class)
public class CustomErrorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHandleError() throws Exception {
        mockMvc.perform(get("/error")
                        .with(user("username")))
                .andExpect(status().is5xxServerError());
    }
}
