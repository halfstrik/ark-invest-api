package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.Fund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public class FundRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Fund> findAll() {
        return jdbcTemplate.query("SELECT * FROM fund", DataClassRowMapper.newInstance(Fund.class));
    }

    public UUID create(String name, String description) {
        UUID id = UUID.randomUUID();
        String sql = """
            INSERT INTO fund (id, name, description)
            VALUES (?, ?, ?)
        """;

        jdbcTemplate.update(sql, id, name, description);
        return id;
    }

    public Optional<Fund> findById(UUID id) {
        String sql = "SELECT * FROM fund WHERE id = ?";
        return jdbcTemplate.query(sql, DataClassRowMapper.newInstance(Fund.class), id)
                .stream()
                .findFirst();
    }
}
