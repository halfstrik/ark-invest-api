package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.FundTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
