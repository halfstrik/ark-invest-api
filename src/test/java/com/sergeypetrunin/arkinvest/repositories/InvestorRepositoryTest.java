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
}
