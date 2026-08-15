package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.Investor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InvestorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Investor> findAll() {
        return jdbcTemplate.query("SELECT * FROM investor", DataClassRowMapper.newInstance(Investor.class));
    }
}
