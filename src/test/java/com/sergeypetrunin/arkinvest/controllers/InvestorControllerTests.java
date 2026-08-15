package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.repositories.InvestorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvestorController.class)
public class InvestorControllerTests {

    @MockitoBean
    InvestorRepository investorRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateInvestorWithValidNameAndEmail() throws Exception {
        UUID id = UUID.randomUUID();
        when(investorRepository.create("Sergey Petrunin", "sergey@example.com")).thenReturn(id);

        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Sergey Petrunin",
                                    "email": "sergey@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/investors/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Sergey Petrunin"))
                .andExpect(jsonPath("$.email").value("sergey@example.com"));
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {
        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Bad Actor",
                                    "email": "notanemail$hack.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
