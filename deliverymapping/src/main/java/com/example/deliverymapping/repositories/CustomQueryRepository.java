package com.example.deliverymapping.repositories;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.deliverymapping.model.DeliveryInfo;
import com.example.deliverymapping.model.DeliveryInfoRowMapper;

@Repository
public class CustomQueryRepository {

    private static String FIND_BY_PROBLEM_ID_SQL_QUERY = """
            SELECT thana || ' - ' || district AS name,
            SUM (product_weight)
            FROM problem_details where problem_id = ? and is_processed = true
            GROUP BY thana, district;
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<DeliveryInfo> findByProblemId(String problemId) {
        try {
            List<DeliveryInfo> transactions = jdbcTemplate.query(FIND_BY_PROBLEM_ID_SQL_QUERY,
                    new DeliveryInfoRowMapper(), problemId);
            return transactions;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}