package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import com.sergeypetrunin.arkinvest.models.TransactionEffect;
import com.sergeypetrunin.arkinvest.models.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
                DataClassRowMapper.newInstance(FundTransaction.class)
        );
    }

    public List<FundTransaction> findByFundId(UUID fundId) {
        return jdbcTemplate.query(
                "SELECT * FROM fund_transaction WHERE fund_id = ?",
                DataClassRowMapper.newInstance(FundTransaction.class),
                fundId
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
                :amount, CURRENT_DATE, :description
            )
            """;
        jdbcClient.sql(sql)
                .param("id", id)
                .param("fund_id", fundId)
                .param("investor_id", investorId)
                .param("transaction_type", transactionType.name())
                .param("transaction_effect", transactionEffect.name())
                .param("amount", amount)
                .param("description", description)
                .update();
        return id;
    }

    private boolean existsFund(UUID id) {
        return jdbcClient.sql("SELECT 1 FROM fund WHERE id = :id")
                .param("id", id)
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
