package com.tsg.spacestation.mappers;

import com.tsg.spacestation.dto.Hashtag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HashtagMapper implements RowMapper<Hashtag> {
    @Override
    public Hashtag mapRow(ResultSet rs, int index) throws SQLException {
        Hashtag hashtag = new Hashtag();
        hashtag.setId(rs.getInt("Id"));
        hashtag.setName(rs.getString("name"));

        return hashtag;
    }

}