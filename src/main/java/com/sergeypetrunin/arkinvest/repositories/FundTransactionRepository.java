package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public class FundTransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<FundTransaction> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM fund_transaction",
                DataClassRowMapper.newInstance(FundTransaction.class)
        );
    }

    @Transactional
    public UUID create(
            UUID fundId,
            UUID investorId,
            String transactionType,
            String transactionEffect,
            BigDecimal amount,
            String description
    ) {
        UUID id = UUID.randomUUID();
        String sql = """
            INSERT INTO fund_transaction (
                id, fund_id, investor_id, transaction_type, transaction_effect,
                amount, transaction_date, description
            )
            VALUES (
                ?, ?, ?,
                ?, ?,
                ?, CURRENT_DATE, ?
            )
            """;
        jdbcTemplate.update(sql, id, fundId, investorId, transactionType, transactionEffect, amount, description);
        return id;
    }
}
