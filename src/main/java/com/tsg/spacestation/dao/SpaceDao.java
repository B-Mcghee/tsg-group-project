package com.tsg.spacestation.dao;

import com.tsg.spacestation.dto.*;
import java.util.List;

public interface SpaceDao {

    Blog addBlog(Blog blog);
    Blog getBlog(int id);
    List<Blog> getAllBlogs();
    List<Blog> getBlogsForCategories(int id);
    void updateBlog(Blog blog);
    void removeBlog(int id);
    
    Category addCategory(Category category);
    Category getCategory(int id);
    List<Category> getAllCategories();
    void updateCategory(Category category);
    void removeCategory(int id);

    Comment addComment(Comment comment);
    Comment getComment(int id);
    List<Comment> getAllComments();
    void updateComment(Comment comment);
    void removeComment(int id);

    Hashtag addHashtag(Hashtag hashtag);
    Hashtag getHashtag(int id);
    List<Hashtag> getAllHashtags();
    void updateHashtag(Hashtag hashtag);
    void removeHashtag(int id);
    
    Image addImage(Image image);
    Image getImage(int id);
    List<Image> getAllImages();
    void updateImage(Image image);
    void removeImage(int id);
    
    Status addStatus(Status status);
    Status getStatus(int id);
    List<Status> getAllStatuses();
    void updateStatus(Status status);
    void removeStatus(int id);

    User addUser(User user);
    User getUser(int id);
    List<User> getAllUsers();
    void updateUser(User user);
    void removeUser(int id);

}

