package com.tsg.spacestation.mappers;

import com.tsg.spacestation.dto.Image;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageMapper implements RowMapper<Image> {

    @Override
    public Image mapRow(ResultSet rs, int index) throws SQLException {
        Image image = new Image();
        image.setId(rs.getInt("Id"));
        image.setURL(rs.getString("URL"));
        image.setType(rs.getString("Type"));

        return image;
    }



}
