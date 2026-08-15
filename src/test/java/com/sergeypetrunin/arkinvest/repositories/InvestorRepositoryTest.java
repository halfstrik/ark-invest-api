package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.Investor;
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
class InvestorRepositoryTest {

    @Autowired
    InvestorRepository repository;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void cleanDatabase() {
        jdbc.sql("DELETE FROM investor").update();
    }

    @Test
    void shouldLoadPreCreatedInvestor() {
        var investor = new Investor(
                UUID.randomUUID(),
                "Sergey Petrunin",
                "sergey@example.com"
        );
        jdbc.sql("""
            INSERT INTO investor (id, name, email)
            VALUES (:id, :name, :email)
            """)
                .param("id", investor.id().toString())
                .param("name", investor.name())
                .param("email", investor.email())
                .update();

        var result = repository.findAll();

        assertThat(result).containsExactly(investor);
    }

    @Test
    void shouldCreateInvestorAndPersistIt() {
        String name = "Jane Doe";
        String email = "jane@example.com";

        UUID id = repository.create(name, email);

        var result = repository.findAll();

        assertThat(result).containsExactly(new Investor(id, name, email));
    }

    @Test
    void shouldRejectDuplicateInvestorEmail() {
        String email = "duplicate@example.com";

        repository.create("First Investor", email);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                repository.create("Second Investor", email));
    }

    @Test
    void shouldFindInvestorByIdWhenExists() {
        String name = "John Doe";
        String email = "john@example.com";

        UUID id = repository.create(name, email);

        var result = repository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(new Investor(id, name, email));
    }

    @Test
    void shouldReturnEmptyWhenInvestorByIdDoesNotExist() {
        UUID id = UUID.randomUUID();

        var result = repository.findById(id);

        assertThat(result).isEmpty();
    }
}
