package com.tsg.spacestation.dao;

import com.tsg.spacestation.dto.*;
import com.tsg.spacestation.mappers.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SpaceDaoImpl implements SpaceDao {

    @Autowired
    @Getter
    JdbcTemplate heySql;

    /*
     ____  __     __    ___ 
    (  _ \(  )   /  \  / __)
     ) _ (/ (_/\(  O )( (_ \
    (____/\____/ \__/  \___/

     */
    @Override
    @Transactional
    public Blog addBlog(Blog blog) {
        final String INSERT_BLOG_INTO_BLOGS = "INSERT INTO blogs (UserId, StatusId, Title, Body, PostedTime, ExpirationTime) VALUES (?, ?, ?, ?, ?, ?)";
        heySql.update(INSERT_BLOG_INTO_BLOGS, blog.getUserId(), blog.getStatus().getId(), blog.getTitle(), blog.getBody(), blog.getDatePosted(), blog.getPostExpired());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        blog.setId(newId);
        this.addCategoryToBlog(blog.getCategoryId(), blog.getId());
        addHashtagsToBlog(blog.getHashtags(), blog.getId());
        return blog;
    }

    @Override
    public Blog getBlog(int id) {
        final String GET_A_BLOG = "SELECT * FROM blogs WHERE Id = ?";
        Blog aBlog = heySql.queryForObject(GET_A_BLOG, new BlogMapper(), id);
        int catId = this.getCategoryForBlog(aBlog.getId());
        aBlog.setCategoryId(catId);

        List<Hashtag> hashtagsForBlog = this.getAllHashtagsForBlog(aBlog.getId());
        aBlog.setHashtags(hashtagsForBlog);

        List<Image> imagesForBlog = this.getAllImagesForBlog(aBlog.getId());
        aBlog.setImages(imagesForBlog);
//        aBlog.setUserId(getUserIdForBlog(id));
        aBlog.setStatus(getStatusForBlog(id));
        return aBlog;
    }

    @Override
    public List<Blog> getAllBlogs() {
        final String GET_ALL_BLOGS = "SELECT * FROM blogs";
        List<Blog> blogs = heySql.query(GET_ALL_BLOGS, new BlogMapper());
        for (Blog aBlog : blogs) {
            int catId = this.getCategoryForBlog(aBlog.getId());
            aBlog.setCategoryId(catId);
            aBlog.setStatus(getStatusForBlog(aBlog.getId()));

            List<Hashtag> hashtagsForBlog = this.getAllHashtagsForBlog(aBlog.getId());
            aBlog.setHashtags(hashtagsForBlog);

            List<Image> imagesForBlog = this.getAllImagesForBlog(aBlog.getId());
            aBlog.setImages(imagesForBlog);
        }

        return blogs;
    }

    @Override
    public void updateBlog(Blog blog) {
        final String UPDATE_A_BLOG = "UPDATE blogs "
                + "SET UserId = ?, StatusId = ?, Title = ?, Body = ?, PostedTime = ?, ExpirationTime = ? "
                + "WHERE Id = ?";
        heySql.update(UPDATE_A_BLOG, blog.getUserId(), blog.getStatus().getId(), blog.getTitle(), blog.getBody(), blog.getDatePosted(), blog.getPostExpired(),
                blog.getId());

        this.removeAllHashtagsFromBlog(blog.getId());
        this.addHashtagsToBlog(blog.getHashtags(), blog.getId());

        this.removeAllImagesFromBlog(blog.getId());
        this.addImagesToBlog(blog.getImages(), blog.getId());

        this.removeCategoryFromBlog(blog.getId());
        this.addCategoryToBlog(blog.getCategoryId(), blog.getId());
    }

    @Override
    @Transactional
    public void removeBlog(int id) {
        this.removeAllHashtagsFromBlog(id);
        this.removeAllImagesFromBlog(id);
        this.removeCategoryFromBlog(id);
        this.deleteCommentsOfBlog(id);
        final String DELETE_BLOG = "DELETE FROM blogs WHERE Id = ?";

        heySql.update(DELETE_BLOG, id);
    }

    @Override
    public List<Blog> getBlogsForCategories(int catId) {
        String BLOGS_FOR_CATEGORY = "SELECT blogs.* FROM Blogs "
                + "JOIN blogcat on blogcat.blogid = blogs.id "
                + "WHERE blogcat.catid = ?";
        List<Blog> catBlogs = heySql.query(BLOGS_FOR_CATEGORY, new BlogMapper(), catId);
        return catBlogs;
    }

//    private int getUserIdForBlog(int blogId){
//        String BLOG_USER_ID = "SELECT USERS.* FROM USERS JOIN BLOGS ON USERS.ID = BLOGS.USERID WHERE BLOGS.ID = ?";
//        User user = heySql.queryForObject(BLOG_USER_ID, new UserMapper(), blogId);
//        return user.getId();
//    }
    private Status getStatusForBlog(int id) {
        String STATUS_ID_FOR_BLOG = "SELECT STATUSES.* FROM STATUSES JOIN BLOGS ON STATUSES.ID = BLOGS.STATUSID WHERE BLOGS.ID = ?";
        Status status = heySql.queryForObject(STATUS_ID_FOR_BLOG, new StatusMapper(), id);
        return status;
    }

    /*
      ___   __  ____  ____  ___   __  ____  _  _ 
     / __) / _\(_  _)(  __)/ __) /  \(  _ \( \/ )
    ( (__ /    \ )(   ) _)( (_ \(  O ))   / )  / 
     \___)\_/\_/(__) (____)\___/ \__/(__\_)(__/  

     */
    @Override
    @Transactional
    public Category addCategory(Category category) {
        final String INSERT_CATEGORY_INTO_CATEGORY = "INSERT INTO categories (Name) VALUES (?)";
        heySql.update(INSERT_CATEGORY_INTO_CATEGORY, category.getName());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        category.setId(newId);
        return category;
    }

    @Override
    public Category getCategory(int id) {
        final String GET_A_CATEGORY = "SELECT * FROM categories WHERE Id = ?";
        Category category = heySql.queryForObject(GET_A_CATEGORY, new CategoryMapper(), id);
        return category;
    }

    @Override
    public List<Category> getAllCategories() {
        final String GET_ALL_CATEGORIES = "SELECT * FROM categories";
        List<Category> categories = heySql.query(GET_ALL_CATEGORIES, new CategoryMapper());
        return categories;
    }

    @Override
    public void updateCategory(Category category) {
        final String UPDATE_A_CATEGORY = "UPDATE categories "
                + "SET Name = ? "
                + "WHERE Id = ?";

        heySql.update(UPDATE_A_CATEGORY, category.getName(), category.getId());
    }

    @Override
    @Transactional
    public void removeCategory(int id) {

        // delete blogs with this category id
        List<Blog> blogs = this.getAllBlogsForCategoryThatAreApproved(id);
        for (Blog blogToDelete : blogs) {

                this.removeBlog(blogToDelete.getId());

        }
        final String DELETE_CATEGORY = "DELETE FROM categories WHERE Id = ?";
        heySql.update(DELETE_CATEGORY, id);
    }

    /*
      ___  __   _  _  _  _  ____  __ _  ____ 
     / __)/  \ ( \/ )( \/ )(  __)(  ( \(_  _)
    ( (__(  O )/ \/ \/ \/ \ ) _) /    /  )(  
     \___)\__/ \_)(_/\_)(_/(____)\_)__) (__) 

     */
    @Override
    @Transactional
    public Comment addComment(Comment comment) {
        final String INSERT_COMMENT_INTO_COMMENT = "INSERT INTO comments (UserId, BlogId, Body, PostedTime) VALUES (?, ?, ?, ?)";
        heySql.update(INSERT_COMMENT_INTO_COMMENT, comment.getUserId(), comment.getBlogId(), comment.getBody(), comment.getPostedTime());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        comment.setId(newId);
        return comment;
    }

    @Override
    public Comment getComment(int id) {
        final String GET_A_COMMENT = "SELECT * FROM comments WHERE Id = ?";
        Comment comment = heySql.queryForObject(GET_A_COMMENT, new CommentMapper(), id);
        return comment;
    }

    @Override
    public List<Comment> getAllComments() {
        final String GET_ALL_COMMENTS = "SELECT * FROM comments";
        List<Comment> comments = heySql.query(GET_ALL_COMMENTS, new CommentMapper());
        return comments;
    }

    @Override
    public void updateComment(Comment comment) {
        final String UPDATE_COMMENT = "UPDATE comments "
                + "SET UserId = ?, BlogId = ?, Body = ?, PostedTime = ? "
                + "WHERE Id = ?";

        heySql.update(UPDATE_COMMENT,
                comment.getUserId(), comment.getBlogId(), comment.getBody(), comment.getPostedTime(),
                comment.getId());
    }

    @Override
    @Transactional
    public void removeComment(int id) {
        final String DELETE_COMMENT = "DELETE FROM comments WHERE Id = ?";
        heySql.update(DELETE_COMMENT, id);
    }

    /*
     _  _   __   ____  _  _  ____  __    ___ 
    / )( \ / _\ / ___)/ )( \(_  _)/ _\  / __)
    ) __ (/    \\___ \) __ (  )( /    \( (_ \
    \_)(_/\_/\_/(____/\_)(_/ (__)\_/\_/ \___/

     */
    @Override
    @Transactional
    public Hashtag addHashtag(Hashtag hashtag) {
        final String INSERT_HASH_INTO_HASH = "INSERT INTO hashtags (Name) VALUES (?)";
        heySql.update(INSERT_HASH_INTO_HASH, hashtag.getName());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        hashtag.setId(newId);
        return hashtag;
    }

    @Override
    public Hashtag getHashtag(int id) {
        final String GET_A_HASHTAG = "SELECT * FROM hashtags WHERE Id = ?";
        Hashtag aHashtag = heySql.queryForObject(GET_A_HASHTAG, new HashtagMapper(), id);
        return aHashtag;
    }

    @Override
    public List<Hashtag> getAllHashtags() {
        final String GET_ALL_HASHTAGS = "SELECT * FROM hashtags";
        List<Hashtag> hashtags = heySql.query(GET_ALL_HASHTAGS, new HashtagMapper());
        return hashtags;
    }

    @Override
    public void updateHashtag(Hashtag hashtag) {
        final String UPDATE_A_HASHTAG = "UPDATE hashtags "
                + "SET Name = ? "
                + "WHERE Id = ?";
        heySql.update(UPDATE_A_HASHTAG, hashtag.getName(), hashtag.getId());
    }

    @Override
    @Transactional
    public void removeHashtag(int id) {
        this.removeAllBlogsFromHashtag(id);
        final String DELETE_HASHTAG = "DELETE FROM hashtags WHERE Id = ?";
        heySql.update(DELETE_HASHTAG, id);
    }

    /*
      __  _  _   __    ___  ____ 
     (  )( \/ ) / _\  / __)(  __)
      )( / \/ \/    \( (_ \ ) _) 
     (__)\_)(_/\_/\_/ \___/(____)

     */
    @Override
    @Transactional
    public Image addImage(Image image) {
        final String INSERT_IMAGE_INTO_IMAGE = "INSERT INTO imageurls (URL, Type) VALUES (?, ?)";
        heySql.update(INSERT_IMAGE_INTO_IMAGE, image.getURL(), image.getType());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        image.setId(newId);
        return image;
    }

    @Override
    public Image getImage(int id) {
        final String GET_AN_IMAGE = "SELECT * FROM imageurls WHERE Id = ?";
        Image anImage = heySql.queryForObject(GET_AN_IMAGE, new ImageMapper(), id);
        return anImage;
    }

    @Override
    public List<Image> getAllImages() {
        final String GET_ALL_IMAGES = "SELECT * FROM imageurls";
        List<Image> images = heySql.query(GET_ALL_IMAGES, new ImageMapper());
        return images;
    }

    @Override
    public void updateImage(Image image) {
        final String UPDATE_AN_IMAGE = "UPDATE imageurls "
                + "SET URL = ?, Type = ? "
                + "WHERE Id = ?";
        heySql.update(UPDATE_AN_IMAGE, image.getURL(), image.getType(), image.getId());
    }

    @Override
    @Transactional
    public void removeImage(int id) {
        this.removeAllBlogsFromImages(id);
        final String DELETE_IMAGE = "DELETE FROM imageurls WHERE Id = ?";
        heySql.update(DELETE_IMAGE, id);
    }

    /*
     ____  ____  __  ____  _  _  ____ 
    / ___)(_  _)/ _\(_  _)/ )( \/ ___)
    \___ \  )( /    \ )(  ) \/ (\___ \
    (____/ (__)\_/\_/(__) \____/(____/

     */
    @Override
    @Transactional
    public Status addStatus(Status status) {
        final String INSERT_STATUS_INTO_STATUS = "INSERT INTO statuses (Name) VALUES (?)";
        heySql.update(INSERT_STATUS_INTO_STATUS, status.getName());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        status.setId(newId);
        return status;
    }

    @Override
    public Status getStatus(int id) {
        final String GET_A_STATUS = "SELECT * FROM statuses WHERE Id = ?";
        Status aStatus = heySql.queryForObject(GET_A_STATUS, new StatusMapper(), id);
        return aStatus;
    }

    @Override
    public List<Status> getAllStatuses() {
        final String GET_ALL_STATUSES = "SELECT * FROM statuses";
        List<Status> statuses = heySql.query(GET_ALL_STATUSES, new StatusMapper());
        return statuses;
    }

    @Override
    public void updateStatus(Status status) {
        final String UPDATE_A_STATUS = "UPDATE statuses "
                + "SET Name = ? "
                + "WHERE Id = ?";
        heySql.update(UPDATE_A_STATUS, status.getName(), status.getId());
    }

    @Override
    @Transactional
    public void removeStatus(int id) {
        this.setBlogsToNAStatus(id);
        final String DELETE_STATUS = "DELETE FROM statuses WHERE Id = ?";
        heySql.update(DELETE_STATUS, id);
    }

    /*
     _  _  ____  ____  ____ 
    / )( \/ ___)(  __)(  _ \
    ) \/ (\___ \ ) _)  )   /
    \____/(____/(____)(__\_)

     */
    @Override
    @Transactional
    public User addUser(User user) {
        final String INSERT_USER_INTO_USER = "INSERT INTO users (DisplayName, UserName, Password, Email) VALUES (?, ?, ?, ?)";
        heySql.update(INSERT_USER_INTO_USER, user.getDisplayName(), user.getUsername(), user.getPassword(), user.getEmail());
        int newId = heySql.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        user.setId(newId);
        return user;
    }

    @Override
    public User getUser(int id) {
        final String GET_A_USER = "SELECT * FROM users WHERE Id = ?";
        User aUser = heySql.queryForObject(GET_A_USER, new UserMapper(), id);
        return aUser;
    }

    @Override
    public List<User> getAllUsers() {
        final String GET_ALL_USERS = "SELECT * FROM users";
        List<User> users = heySql.query(GET_ALL_USERS, new UserMapper());
        return users;
    }

    @Override
    public void updateUser(User user) {
        final String UPDATE_A_USER = "UPDATE users "
                + "SET DisplayName = ?, UserName = ?, Password = ?, Email = ? "
                + "WHERE Id = ?";

        heySql.update(UPDATE_A_USER,
                user.getDisplayName(), user.getUsername(), user.getPassword(), user.getEmail(),
                user.getId());
    }

    @Override
    @Transactional
    public void removeUser(int id) {
        this.setBlogsToNAUser(id);
        this.removeAllCommentsForDeletedUser(id);
        final String DELETE_USER = "DELETE FROM users WHERE Id = ?";
        heySql.update(DELETE_USER, id);
    }

    /*
     _  _  ____  __    ____  ____  ____  ____ 
    / )( \(  __)(  )  (  _ \(  __)(  _ \/ ___)
    ) __ ( ) _) / (_/\ ) __/ ) _)  )   /\___ \
    \_)(_/(____)\____/(__)  (____)(__\_)(____/

     */
    // hashtag helpers
    private void removeAllBlogsFromHashtag(int hashtagId) {
        String REMOVE_BLOGS_FROM_HASHTAG = "DELETE FROM bloghash WHERE HashtagId = ?";
        heySql.update(REMOVE_BLOGS_FROM_HASHTAG, hashtagId);
    }

    // image helpers
    private void removeAllBlogsFromImages(int ImageId) {
        String REMOVE_BLOGS_FROM_IMAGES = "DELETE FROM blogimg WHERE ImgId = ?";
        heySql.update(REMOVE_BLOGS_FROM_IMAGES, ImageId);
    }

    // status helpers
    private void setBlogsToNAStatus(int statusId) {

        String UPDATE_BLOG_TO_NA_STATUS = "UPDATE blogs SET StatusId = '-1' WHERE statusId = ?";
        heySql.update(UPDATE_BLOG_TO_NA_STATUS, statusId);
    }

    // user helpers
    private void setBlogsToNAUser(int userId) {
        String UPDATE_BLOG_TO_NA_USER = "UPDATE blogs SET UserId = '-1' WHERE UserId = ?";
        heySql.update(UPDATE_BLOG_TO_NA_USER, userId);
    }

    private void removeAllCommentsForDeletedUser(int userId) {
        String REMOVE_COMMENTS_FROM_DELETED_USER = "DELETE FROM comments WHERE UserId = ?";
        heySql.update(REMOVE_COMMENTS_FROM_DELETED_USER, userId);
    }

    // category helpers
    private void removeBlogsAndCatAssociation(int catId) {
        String REMOVE_BLOGS_FROM_CAT = "DELETE FROM blogcat WHERE CatId = ?";
        heySql.update(REMOVE_BLOGS_FROM_CAT, catId);
    }

    // blog helpers
    private int getCategoryForBlog(int blogId) {
        String CATEGORY_FOR_BLOG = "SELECT * FROM categories "
                + "JOIN blogcat ON blogcat.CatId = categories.Id "
                + "WHERE blogcat.BlogId = ?";
        List<Category> blogCats = heySql.query(CATEGORY_FOR_BLOG, new CategoryMapper(), blogId);
        return blogCats.get(0).getId();
    }
    public List<Blog> getAllBlogsForCategoryThatAreApproved(int catId){
        String ALL_BLOGS_FOR_CATEGORY = "SELECT blogs.* FROM Blogs "
                + "JOIN blogcat ON blogcat.blogid = blogs.id "
                + "WHERE blogcat.catid = ? AND blogs.StatusId = 1";
        List<Blog> blogs = heySql.query(ALL_BLOGS_FOR_CATEGORY, new BlogMapper(), catId);
        return blogs;
    }
    private List<Hashtag> getAllHashtagsForBlog(int blogId) {
        String HASHTAGS_FOR_BLOG = "SELECT * FROM hashtags "
                + "JOIN bloghash ON bloghash.HashTagId = hashtags.Id "
                + "WHERE bloghash.BlogId = ?";
        return heySql.query(HASHTAGS_FOR_BLOG, new HashtagMapper(), blogId);
    }

    private List<Image> getAllImagesForBlog(int blogId) {
        String IMAGES_FOR_BLOG = "SELECT * FROM imageurls "
                + "JOIN blogimg ON blogimg.ImgId = imageurls.Id "
                + "WHERE blogimg.BlogId = ?";
        return heySql.query(IMAGES_FOR_BLOG, new ImageMapper(), blogId);
    }

    private void removeAllHashtagsFromBlog(int blogId) {
        String REMOVE_HASHTAGS_FROM_BlOG = "DELETE FROM bloghash WHERE BlogId = ?";
        heySql.update(REMOVE_HASHTAGS_FROM_BlOG, blogId);
    }

    private void removeAllImagesFromBlog(int blogId) {
        String REMOVE_IMAGES_FROM_BlOG = "DELETE FROM blogimg WHERE BlogId = ?";
        heySql.update(REMOVE_IMAGES_FROM_BlOG, blogId);
    }

    private void removeCategoryFromBlog(int blogId) {
        String REMOVE_CATEGORIES_FROM_BlOG = "DELETE FROM blogcat WHERE BlogId = ?";
        heySql.update(REMOVE_CATEGORIES_FROM_BlOG, blogId);
    }

    private void deleteCommentsOfBlog(int blogId) {
        String DELETE_COMMENTS_OF_BLOG = "DELETE FROM comments WHERE BlogId = ?";
        heySql.update(DELETE_COMMENTS_OF_BLOG, blogId);
    }

    private void addHashtagsToBlog(List<Hashtag> hashtags, int blogId) {
        for (Hashtag aHashtag : hashtags) {
            String HASHTAG_BLOG = "INSERT INTO bloghash (BlogId, HashtagId) VALUES (?, ?)";
            heySql.update(HASHTAG_BLOG, blogId, aHashtag.getId());
        }
    }

    private void addImagesToBlog(List<Image> images, int blogId) {
        for (Image anImage : images) {
            String IMAGE_BLOG = "INSERT INTO blogimg (BlogId, ImgId) VALUES (?, ?)";
            heySql.update(IMAGE_BLOG, blogId, anImage.getId());
        }
    }

    private void addCategoryToBlog(int categoryId, int blogId) {
        String CAT_BLOG = "INSERT INTO blogcat (BlogId, CatId) VALUES (?, ?)";
        heySql.update(CAT_BLOG, blogId, categoryId);
    }
}
