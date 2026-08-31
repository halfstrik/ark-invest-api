package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.repositories.FundPermissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FundPermissionController.class)
public class FundPermissionControllerTests {

    @MockitoBean
    FundPermissionRepository fundPermissionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateFundPermission() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();
        when(fundPermissionRepository.create(fundId, investorId)).thenReturn(true);

        mockMvc.perform(post("/fund-permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fund_id": "%s",
                                    "investor_id": "%s"
                                }
                                """.formatted(fundId, investorId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/fund-permissions"))
                .andExpect(jsonPath("$.fund_id").value(fundId.toString()))
                .andExpect(jsonPath("$.investor_id").value(investorId.toString()));
    }

    @Test
    void shouldReturn200WhenFundPermissionAlreadyExists() throws Exception {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();
        when(fundPermissionRepository.create(fundId, investorId)).thenReturn(false);

        mockMvc.perform(post("/fund-permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fund_id": "%s",
                                    "investor_id": "%s"
                                }
                                """.formatted(fundId, investorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fund_id").value(fundId.toString()))
                .andExpect(jsonPath("$.investor_id").value(investorId.toString()));
    }
}
