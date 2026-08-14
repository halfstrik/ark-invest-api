package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.Fund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FundRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Fund> findAll() {
        return jdbcTemplate.query("SELECT * FROM fund", DataClassRowMapper.newInstance(Fund.class));
    }

}
