package com.sergeypetrunin.arkinvest.controllers;

import com.sergeypetrunin.arkinvest.models.Investor;
import com.sergeypetrunin.arkinvest.repositories.InvestorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .andExpect(jsonPath("$.email").value("sergey@example.com"))
                .andExpect(jsonPath("$.is_deleted").value(false));
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

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        when(investorRepository.create("Duplicate Investor", "duplicate@example.com"))
                .thenThrow(new IllegalArgumentException("Investor with email 'duplicate@example.com' already exists"));

        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Duplicate Investor",
                                    "email": "duplicate@example.com"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExistsAtDatabaseLevel() throws Exception {
        when(investorRepository.create("Duplicate Investor", "duplicate@example.com"))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));

        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Duplicate Investor",
                                    "email": "duplicate@example.com"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsNull() throws Exception {
        when(investorRepository.create(null, "noname@example.com"))
                .thenThrow(new DataIntegrityViolationException("null value in column \"name\" violates not-null constraint"));

        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "noname@example.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        when(investorRepository.create("", "blank@example.com"))
                .thenThrow(new DataIntegrityViolationException("null value in column \"name\" violates not-null constraint"));

        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "email": "blank@example.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsNull() throws Exception {
        when(investorRepository.create("No Email", null))
                .thenThrow(new DataIntegrityViolationException("null value in column \"email\" violates not-null constraint"));

        mockMvc.perform(post("/investors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "No Email"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetInvestorByIdWhenExists() throws Exception {
        UUID id = UUID.randomUUID();
        Investor investor = new Investor(id, "Sergey Petrunin", "sergey@example.com", false);
        when(investorRepository.findById(id)).thenReturn(Optional.of(investor));

        mockMvc.perform(get("/investors/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Sergey Petrunin"))
                .andExpect(jsonPath("$.email").value("sergey@example.com"))
                .andExpect(jsonPath("$.is_deleted").value(false));
    }

    @Test
    void shouldReturn404WhenInvestorByIdDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(investorRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/investors/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSoftDeleteInvestor() throws Exception {
        UUID id = UUID.randomUUID();
        Investor investor = new Investor(id, "Sergey Petrunin", "sergey@example.com", true);
        when(investorRepository.softDelete(id)).thenReturn(Optional.of(investor));

        mockMvc.perform(delete("/investors/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.is_deleted").value(true));
    }

    @Test
    void shouldReturn404WhenSoftDeletingNonExistingInvestor() throws Exception {
        UUID id = UUID.randomUUID();
        when(investorRepository.softDelete(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/investors/" + id))
                .andExpect(status().isNotFound());
    }
}
