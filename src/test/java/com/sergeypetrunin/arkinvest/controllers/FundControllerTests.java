package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.Fund;
import com.sergeypetrunin.arkinvest.repositories.FundRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FundController.class)
public class FundControllerTests {

    @MockitoBean
    FundRepository fundRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetFunds() throws Exception {
        mockMvc.perform(get("/funds"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateFund() throws Exception {
        UUID id = UUID.randomUUID();
        when(fundRepository.create("Growth Fund", "A fund focused on growth stocks")).thenReturn(id);

        mockMvc.perform(post("/funds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Growth Fund",
                                    "description": "A fund focused on growth stocks"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/funds/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Growth Fund"))
                .andExpect(jsonPath("$.description").value("A fund focused on growth stocks"));
    }

    @Test
    void shouldGetFundByIdWhenExists() throws Exception {
        UUID id = UUID.randomUUID();
        Fund fund = new Fund(id, "Innovation Fund", "Focuses on disruptive innovation");
        when(fundRepository.findById(id)).thenReturn(Optional.of(fund));

        mockMvc.perform(get("/funds/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Innovation Fund"))
                .andExpect(jsonPath("$.description").value("Focuses on disruptive innovation"));
    }

    @Test
    void shouldReturn404WhenFundByIdDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(fundRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/funds/{id}", id))
                .andExpect(status().isNotFound());
    }

}
