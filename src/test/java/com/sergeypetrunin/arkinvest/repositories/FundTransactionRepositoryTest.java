package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.models.TransactionEffect;
import com.sergeypetrunin.arkinvest.models.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
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
                TransactionType.CONTRIBUTION,
                TransactionEffect.CREDIT,
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
                .param("transaction_type", transaction.transactionType().name())
                .param("transaction_effect", transaction.transactionEffect().name())
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
                TransactionType.CONTRIBUTION,
                TransactionEffect.CREDIT,
                new BigDecimal("100.5"),
                "Initial contribution"
        );

        var result = repository.findAll();

        assertThat(result).hasSize(1);
        var transaction = result.get(0);
        assertThat(transaction.id()).isEqualTo(transactionId);
        assertThat(transaction.fundId()).isEqualTo(fundId);
        assertThat(transaction.investorId()).isEqualTo(investorId);
        assertThat(transaction.transactionType()).isEqualTo(TransactionType.CONTRIBUTION);
        assertThat(transaction.transactionEffect()).isEqualTo(TransactionEffect.CREDIT);
        assertThat(transaction.amount()).isEqualByComparingTo(new BigDecimal("100.5"));
        assertThat(transaction.description()).isEqualTo("Initial contribution");
        assertThat(transaction.transactionDate()).isNotNull();
    }

    @Test
    void shouldFindOnlyTransactionsForRequestedFund() {
        UUID firstFundId = UUID.randomUUID();
        UUID secondFundId = UUID.randomUUID();
        UUID firstInvestorId = UUID.randomUUID();
        UUID secondInvestorId = UUID.randomUUID();

        jdbc.sql("INSERT INTO fund (id, name, description) VALUES (:id, :name, :description)")
                .param("id", firstFundId.toString())
                .param("name", "First Fund")
                .param("description", "First test fund")
                .update();

        jdbc.sql("INSERT INTO fund (id, name, description) VALUES (:id, :name, :description)")
                .param("id", secondFundId.toString())
                .param("name", "Second Fund")
                .param("description", "Second test fund")
                .update();

        jdbc.sql("INSERT INTO investor (id, name, email) VALUES (:id, :name, :email)")
                .param("id", firstInvestorId.toString())
                .param("name", "First Investor")
                .param("email", "first@example.com")
                .update();

        jdbc.sql("INSERT INTO investor (id, name, email) VALUES (:id, :name, :email)")
                .param("id", secondInvestorId.toString())
                .param("name", "Second Investor")
                .param("email", "second@example.com")
                .update();

        UUID firstTransactionId = repository.create(
                firstFundId,
                firstInvestorId,
                TransactionType.CONTRIBUTION,
                TransactionEffect.CREDIT,
                new BigDecimal("100.0"),
                "First fund contribution"
        );

        repository.create(
                secondFundId,
                secondInvestorId,
                TransactionType.CONTRIBUTION,
                TransactionEffect.CREDIT,
                new BigDecimal("200.0"),
                "Second fund contribution"
        );

        var result = repository.findByFundId(firstFundId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(firstTransactionId);
        assertThat(result.get(0).fundId()).isEqualTo(firstFundId);
        assertThat(result.get(0).description()).isEqualTo("First fund contribution");
    }
}
