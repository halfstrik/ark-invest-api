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
}
