package com.tsg.spacestation.mappers;

import com.tsg.spacestation.dto.Status;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusMapper implements RowMapper<Status> {

    @Override
    public Status mapRow(ResultSet rs, int index) throws SQLException {
        Status status = new Status();
        status.setId(rs.getInt("Id"));
        status.setName(rs.getString("Name"));

        return status;
    }

}