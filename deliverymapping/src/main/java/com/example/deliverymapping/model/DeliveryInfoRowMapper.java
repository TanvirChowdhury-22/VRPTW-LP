package com.example.deliverymapping.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class DeliveryInfoRowMapper implements RowMapper<DeliveryInfo> {

    @Override
    public DeliveryInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        DeliveryInfo bankCity = new DeliveryInfo(rs.getString("name"),
                                            rs.getDouble("sum"));
        return bankCity;
    }
}
