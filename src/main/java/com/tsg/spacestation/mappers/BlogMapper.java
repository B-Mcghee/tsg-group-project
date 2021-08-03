package com.tsg.spacestation.mappers;

import com.tsg.spacestation.dto.Blog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BlogMapper implements RowMapper<Blog> {

    @Override
    public Blog mapRow(ResultSet rs, int index) throws SQLException {
        Blog blog = new Blog();
        blog.setId(rs.getInt("Id"));
        blog.setTitle(rs.getString("title"));
        blog.setUserId(rs.getInt("UserId"));
        blog.setBody(rs.getString("body"));
        blog.setDatePosted(rs.getTimestamp("postedtime").toLocalDateTime());
        blog.setPostExpired(rs.getTimestamp("expirationtime").toLocalDateTime());


        

        return blog;
    }

}