package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.Fund;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FundRepositoryTest {

    @Autowired
    FundRepository repository;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void cleanDatabase() {
        jdbc.sql("DELETE FROM fund").update();
    }

    @Test
    void shouldSaveAndFindFund() {
        var fund = new Fund(
                UUID.randomUUID(),
                "Retirement Fund",
                "Test fund"
        );
        jdbc.sql("""
            INSERT INTO fund (id, name, description)
            VALUES (:id, :name, :desc)
            """)
                .param("id", fund.id().toString())
                .param("name", fund.name())
                .param("desc", fund.description())
                .update();

        var result = repository.findAll();

        assertThat(result).containsExactly(fund);
    }

    @Test
    void shouldCreateFund() {
        String name = "Growth Fund";
        String description = "A fund focused on growth stocks";

        UUID id = repository.create(name, description);

        var result = repository.findAll();

        assertThat(result).containsExactly(new Fund(id, name, description));
    }

    @Test
    void shouldFindFundByIdWhenExists() {
        var fund = new Fund(
                UUID.randomUUID(),
                "Innovation Fund",
                "Focuses on disruptive innovation"
        );
        jdbc.sql("""
            INSERT INTO fund (id, name, description)
            VALUES (:id, :name, :desc)
            """)
                .param("id", fund.id().toString())
                .param("name", fund.name())
                .param("desc", fund.description())
                .update();

        var result = repository.findById(fund.id());

        assertThat(result).isPresent().contains(fund);
    }

    @Test
    void shouldReturnEmptyWhenFundByIdDoesNotExist() {
        var result = repository.findById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldRejectDuplicateFundName() {
        String name = "Unique Fund";
        String description = "Original description";

        repository.create(name, description);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                repository.create(name, "Another description"));
    }

    @Test
    void shouldUpdateDescription() {
        var fund = new Fund(
                UUID.randomUUID(),
                "Innovation Fund",
                "Original description"
        );
        jdbc.sql("""
            INSERT INTO fund (id, name, description)
            VALUES (:id, :name, :desc)
            """)
                .param("id", fund.id().toString())
                .param("name", fund.name())
                .param("desc", fund.description())
                .update();

        Optional<Fund> result = repository.updateDescription(fund.id(), "Updated description");

        assertThat(result).isPresent();
        assertThat(result.get().description()).isEqualTo("Updated description");
        assertThat(result.get().name()).isEqualTo("Innovation Fund");
    }

    @Test
    void shouldReturnEmptyWhenUpdatingDescriptionForNonExistingFund() {
        Optional<Fund> result = repository.updateDescription(UUID.randomUUID(), "Updated description");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSoftDeleteFund() {
        var fund = new Fund(
                UUID.randomUUID(),
                "Innovation Fund",
                "Focuses on disruptive innovation"
        );
        jdbc.sql("""
            INSERT INTO fund (id, name, description)
            VALUES (:id, :name, :desc)
            """)
                .param("id", fund.id().toString())
                .param("name", fund.name())
                .param("desc", fund.description())
                .update();

        Optional<Fund> result = repository.softDelete(fund.id());

        assertThat(result).isPresent();
        assertThat(result.get().isDeleted()).isTrue();
        assertThat(repository.findById(fund.id())).isEmpty();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenSoftDeletingNonExistingFund() {
        Optional<Fund> result = repository.softDelete(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenSoftDeletingAlreadyDeletedFund() {
        var fund = new Fund(
                UUID.randomUUID(),
                "Innovation Fund",
                "Focuses on disruptive innovation"
        );
        jdbc.sql("""
            INSERT INTO fund (id, name, description)
            VALUES (:id, :name, :desc)
            """)
                .param("id", fund.id().toString())
                .param("name", fund.name())
                .param("desc", fund.description())
                .update();

        repository.softDelete(fund.id());

        Optional<Fund> result = repository.softDelete(fund.id());

        assertThat(result).isEmpty();
    }

}
