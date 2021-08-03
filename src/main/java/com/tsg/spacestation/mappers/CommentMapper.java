package com.tsg.spacestation.mappers;

import com.tsg.spacestation.dto.Comment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CommentMapper implements RowMapper<Comment> {

    @Override
    public Comment mapRow(ResultSet rs, int index) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getInt("Id"));
        comment.setUserId(rs.getInt("Userid"));
        comment.setBlogId(rs.getInt("BlogId"));
        comment.setBody(rs.getString("body"));
        
        Timestamp commentTimestamp = rs.getTimestamp("postedTime");
        commentTimestamp.setNanos(0);
        comment.setPostedTime(commentTimestamp.toLocalDateTime());

        return comment;
    }

}
