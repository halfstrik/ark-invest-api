package com.sergeypetrunin.arkinvest.repositories;

import com.sergeypetrunin.arkinvest.models.Fund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public UUID create(String name, String description) {
        // How it works:
        // 1 The SELECT ?, ?, ? part produces one candidate row with the new id, name, and description.
        // 2 The WHERE NOT EXISTS (...) clause acts as a guard. It searches the fund table for any row with the same name.
        // 3 If a matching name exists, the SELECT returns zero rows, so the INSERT does nothing. jdbcTemplate.update(...) returns 0.
        // 4 If no matching name exists, the SELECT returns one row, and the INSERT inserts it. jdbcTemplate.update(...) returns 1.
        UUID id = UUID.randomUUID();
        String sql = """
            INSERT INTO fund (id, name, description, is_deleted)
            SELECT ?, ?, ?, ?
            WHERE NOT EXISTS (
                SELECT 1 FROM fund WHERE name = ? AND is_deleted = ?
            )
        """;

        int rowsInserted = jdbcTemplate.update(sql, id, name, description, false, name, false);
        if (rowsInserted == 0) {
            throw new IllegalArgumentException("Fund with name '" + name + "' already exists");
        }
        return id;
    }

    public Optional<Fund> findById(UUID id) {
        String sql = "SELECT * FROM fund WHERE id = ?";
        return jdbcTemplate.query(sql, DataClassRowMapper.newInstance(Fund.class), id)
                .stream()
                .findFirst();
    }

    @Transactional
    public Optional<Fund> updateDescription(UUID id, String description) {
        String sql = """
            UPDATE fund
            SET description = ?
            WHERE id = ? AND is_deleted = ?
        """;

        int rowsUpdated = jdbcTemplate.update(sql, description, id, false);
        if (rowsUpdated == 0) {
            return Optional.empty();
        }
        return findById(id);
    }

    @Transactional
    public Optional<Fund> softDelete(UUID id) {
        String updateSql = """
            UPDATE fund
            SET is_deleted = ?
            WHERE id = ? AND is_deleted = ?
        """;

        int rowsUpdated = jdbcTemplate.update(updateSql, true, id, false);
        if (rowsUpdated == 0) {
            return Optional.empty();
        }

        String selectSql = "SELECT * FROM fund WHERE id = ?";
        return jdbcTemplate.query(selectSql, DataClassRowMapper.newInstance(Fund.class), id)
                .stream()
                .findFirst();
    }
}
