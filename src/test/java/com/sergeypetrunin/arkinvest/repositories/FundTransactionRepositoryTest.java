package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FundTransactionRepositoryTest {

    @Autowired
    FundTransactionRepository repository;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void cleanDatabase() {
        jdbc.sql("DELETE FROM fund_transaction").update();
        jdbc.sql("DELETE FROM fund").update();
        jdbc.sql("DELETE FROM investor").update();
    }

    @Test
    void shouldLoadPreCreatedTransaction() {
        UUID fundId = UUID.randomUUID();
        UUID investorId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

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

        var transaction = new FundTransaction(
                transactionId,
                fundId,
                investorId,
                "CONTRIBUTION",
                "CREDIT",
                new BigDecimal("100.5"),
                "2026-08-14",
                "Initial contribution"
        );

        jdbc.sql("""
            INSERT INTO fund_transaction (
                id, fund_id, investor_id, transaction_type, transaction_effect,
                amount, transaction_date, description
            )
            VALUES (:id, :fund_id, :investor_id, :transaction_type, :transaction_effect,
                    :amount, :transaction_date, :description)
            """)
                .param("id", transaction.id().toString())
                .param("fund_id", transaction.fundId().toString())
                .param("investor_id", transaction.investorId().toString())
                .param("transaction_type", transaction.transactionType())
                .param("transaction_effect", transaction.transactionEffect())
                .param("amount", transaction.amount())
                .param("transaction_date", transaction.transactionDate())
                .param("description", transaction.description())
                .update();

        var result = repository.findAll();

        assertThat(result).containsExactly(transaction);
    }

    @Test
    void shouldCreateTransactionAndPersistIt() {
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

        UUID transactionId = repository.create(
                fundId,
                investorId,
                "CONTRIBUTION",
                "CREDIT",
                new BigDecimal("100.5"),
                "Initial contribution"
        );

        var result = repository.findAll();

        assertThat(result).hasSize(1);
        var transaction = result.get(0);
        assertThat(transaction.id()).isEqualTo(transactionId);
        assertThat(transaction.fundId()).isEqualTo(fundId);
        assertThat(transaction.investorId()).isEqualTo(investorId);
        assertThat(transaction.transactionType()).isEqualTo("CONTRIBUTION");
        assertThat(transaction.transactionEffect()).isEqualTo("CREDIT");
        assertThat(transaction.amount()).isEqualByComparingTo(new BigDecimal("100.5"));
        assertThat(transaction.description()).isEqualTo("Initial contribution");
        assertThat(transaction.transactionDate()).isNotNull();
    }
}
