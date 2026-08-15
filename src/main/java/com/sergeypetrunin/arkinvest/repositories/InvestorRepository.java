package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.Investor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class InvestorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Investor> findAll() {
        return jdbcTemplate.query("SELECT * FROM investor", DataClassRowMapper.newInstance(Investor.class));
    }

    @Transactional
    public UUID create(String name, String email) {
        UUID id = UUID.randomUUID();
        String sql = """
            INSERT INTO investor (id, name, email)
            SELECT ?, ?, ?
            WHERE NOT EXISTS (
                SELECT 1 FROM investor WHERE email = ?
            )
        """;

        int rowsInserted = jdbcTemplate.update(sql, id, name, email, email);
        if (rowsInserted == 0) {
            throw new IllegalArgumentException("Investor with email '" + email + "' already exists");
        }
        return id;
    }
}
