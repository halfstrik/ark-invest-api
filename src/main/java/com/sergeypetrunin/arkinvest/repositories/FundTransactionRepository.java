package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.models.TransactionEffect;
import com.sergeypetrunin.arkinvest.models.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class FundTransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcClient jdbcClient;

    public List<FundTransaction> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM fund_transaction",
                this::mapRow
        );
    }

    public List<FundTransaction> findByFundId(UUID fundId) {
        return jdbcTemplate.query(
                "SELECT * FROM fund_transaction WHERE fund_id = ?",
                this::mapRow,
                fundId
        );
    }

    private FundTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FundTransaction(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("fund_id")),
                UUID.fromString(rs.getString("investor_id")),
                TransactionType.valueOf(rs.getString("transaction_type")),
                TransactionEffect.valueOf(rs.getString("transaction_effect")),
                rs.getBigDecimal("amount"),
                rs.getTimestamp("transaction_date").toInstant(),
                rs.getString("description")
        );
    }

    @Transactional
    public UUID create(
        UUID fundId,
        UUID investorId,
        TransactionType transactionType,
        TransactionEffect transactionEffect,
        BigDecimal amount,
        String description
    ) {
        if (!existsFund(fundId)) {
            throw new IllegalArgumentException("Fund with id '" + fundId + "' does not exist");
        }
        if (isFundDeleted(fundId)) {
            throw new IllegalArgumentException("Fund with id '" + fundId + "' is deleted");
        }
        if (!existsInvestor(investorId)) {
            throw new IllegalArgumentException("Investor with id '" + investorId + "' does not exist");
        }

        UUID id = UUID.randomUUID();
        String sql = """
            INSERT INTO fund_transaction (
                id, fund_id, investor_id, transaction_type, transaction_effect,
                amount, transaction_date, description
            )
            VALUES (
                :id, :fund_id, :investor_id, :transaction_type, :transaction_effect,
                :amount, :transaction_date, :description
            )
            """;
        jdbcClient.sql(sql)
                .param("id", id)
                .param("fund_id", fundId)
                .param("investor_id", investorId)
                .param("transaction_type", transactionType.name())
                .param("transaction_effect", transactionEffect.name())
                .param("amount", amount)
                .param("transaction_date", Instant.now())
                .param("description", description)
                .update();
        return id;
    }

    boolean existsFund(UUID id) {
        return jdbcClient.sql("SELECT 1 FROM fund WHERE id = :id")
                .param("id", id)
                .query(Integer.class)
                .optional()
                .isPresent();
    }

    boolean isFundDeleted(UUID id) {
        return jdbcClient.sql("SELECT 1 FROM fund WHERE id = :id AND is_deleted = :is_deleted")
                .param("id", id)
                .param("is_deleted", true)
                .query(Integer.class)
                .optional()
                .isPresent();
    }

    private boolean existsInvestor(UUID id) {
        return jdbcClient.sql("SELECT 1 FROM investor WHERE id = :id")
                .param("id", id)
                .query(Integer.class)
                .optional()
                .isPresent();
    }
}
