package com.sergeypetrunin.arkinvest.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FundPermissionRepositoryTest {

    @Autowired
    FundPermissionRepository repository;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void cleanDatabase() {
        jdbc.sql("DELETE FROM fund_permission").update();
        jdbc.sql("DELETE FROM fund").update();
        jdbc.sql("DELETE FROM investor").update();
    }

    @Test
    void shouldCreatePermission() {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        jdbc.sql("INSERT INTO fund (id, name, description) VALUES (:id, :name, :description)")
                .param("id", fundId.toString())
                .param("name", "Test Fund")
                .param("description", "A test fund")
                .update();

        jdbc.sql("INSERT INTO investor (id, name, email) VALUES (:id, :name, :email)")
                .param("id", investorId.toString())
                .param("name", "Test Investor")
                .param("email", "test@example.com")
                .update();

        boolean created = repository.create(fundId, investorId);

        assertThat(created).isTrue();
        assertThat(repository.hasPermission(fundId, investorId)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenPermissionAlreadyExists() {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        jdbc.sql("INSERT INTO fund (id, name, description) VALUES (:id, :name, :description)")
                .param("id", fundId.toString())
                .param("name", "Test Fund")
                .param("description", "A test fund")
                .update();

        jdbc.sql("INSERT INTO investor (id, name, email) VALUES (:id, :name, :email)")
                .param("id", investorId.toString())
                .param("name", "Test Investor")
                .param("email", "test@example.com")
                .update();

        repository.create(fundId, investorId);
        boolean createdAgain = repository.create(fundId, investorId);

        assertThat(createdAgain).isFalse();
        assertThat(repository.hasPermission(fundId, investorId)).isTrue();
    }

    @Test
    void hasPermissionShouldReturnFalseWhenNoPermission() {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();

        assertThat(repository.hasPermission(fundId, investorId)).isFalse();
    }
}
