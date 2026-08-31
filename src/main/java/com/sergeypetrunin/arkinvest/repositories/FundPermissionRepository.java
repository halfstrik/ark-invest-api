package com.sergeypetrunin.arkinvest.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class FundPermissionRepository {

    @Autowired
    private JdbcClient jdbcClient;

    public boolean hasPermission(UUID fundId, UUID investorId) {
        return jdbcClient.sql("""
                SELECT 1 FROM fund_permission
                WHERE fund_id = :fund_id AND investor_id = :investor_id
                """)
                .param("fund_id", fundId)
                .param("investor_id", investorId)
                .query(Integer.class)
                .optional()
                .isPresent();
    }

    public boolean create(UUID fundId, UUID investorId) {
        if (hasPermission(fundId, investorId)) {
            return false;
        }
        jdbcClient.sql("""
                INSERT INTO fund_permission (fund_id, investor_id)
                VALUES (:fund_id, :investor_id)
                """)
                .param("fund_id", fundId)
                .param("investor_id", investorId)
                .update();
        return true;
    }

    public boolean delete(UUID fundId, UUID investorId) {
        int rowsDeleted = jdbcClient.sql("""
                DELETE FROM fund_permission
                WHERE fund_id = :fund_id AND investor_id = :investor_id
                """)
                .param("fund_id", fundId)
                .param("investor_id", investorId)
                .update();
        return rowsDeleted > 0;
    }
}
