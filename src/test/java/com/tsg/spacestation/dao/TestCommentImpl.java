package com.tsg.spacestation.dao;

import com.tsg.spacestation.dto.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCommentImpl {

    @Autowired
    SpaceDaoImpl spaceDao;

    private User[] testUsers = {
        new User(1, "user1", "1234", "user1@email.com", "theGreatest"),
        new User(2, "user2", "1234", "user2@email.com", "spaceguy"),
        new User(3, "user3", "1234", "user3@email.com", "PlutoIsAPlanet")

    };

    User user1 = testUsers[0];
    User user2 = testUsers[1];
    User user3 = testUsers[2];

    private Hashtag[] testHashtags = {
        new Hashtag(1, "#ILoveRockets"),
        new Hashtag(2, "#NASA"),
        new Hashtag(3, "#SpaceRulez"),
        new Hashtag(4, "#AliensAreReal"),
        new Hashtag(5, "#ShootingStars"),
        new Hashtag(6, "#PlutoIsAPlanet"),
        new Hashtag(7, "#NeilArmstrongDidntReallyGoToTheMoon"),
        new Hashtag(8, "#HiddenFigures")

    };
    Hashtag hashtag1 = testHashtags[0];
    Hashtag hashtag2 = testHashtags[1];
    Hashtag hashtag3 = testHashtags[2];
    Hashtag hashtag4 = testHashtags[3];
    Hashtag hashtag5 = testHashtags[4];
    
    private Image[] testImages ={
            new Image(1,"https://www.google.com/search?q=super+hero+wallpaper&rlz=1C5CHFA" +
                    "_enUS823US823&tbm=isch&source=iu&ictx=1&fir=QF3_UExy3C0uUM%253A%252C","thumbnail"),
            new Image(2,"https://www.google.com/search?q=super+hero+wallpaper&rlz=1C5CHFA" +
                    "_enUS823US823&tbm=isch&source=iu&ictx=1&fir=QF3_UExy3C0uUM%253A%252C","header"),
            new Image(  3,"https://www.google.com/search?q=super+hero+wallpaper&rlz=1C5CHFA","header")

    };
    Image image1 = testImages[0];
    Image image2 = testImages[1];
    Image image3 = testImages[2];

    private Status[] testStatuses = {
        new Status(1, "Approved"),
        new Status(2, "Staged"),
        new Status(3, "Submited"),};
    
    Status status1 = testStatuses[0];
    Status status2 = testStatuses[1];
    Status status3 = testStatuses[2];

    private Category[] testCategories = {
        new Category(1, "To The Moon"),
        new Category(2, "Up Up and Away!"),
        new Category(3, "SpaceX"),
        new Category(4, "All About Babylon")
    };
    
    Category category1 = testCategories[0];
    Category category2 = testCategories[1];
    Category category3 = testCategories[2];
    Category category4 = testCategories[3];

    private Blog[] testBlogs = {
        new Blog(1, "Forever in Space", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
        + "tempor incididunt ut labore et dolore magna aliqua.", status1, user1.getId(), 2, new ArrayList<>(), new ArrayList<>(), LocalDateTime.now().minusDays(10).withNano(0),
        LocalDateTime.now().withNano(0)),
        new Blog(2, "Lost in Space", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
        + "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
        status1, user2.getId(), 3, new ArrayList<>(), new ArrayList<>(), LocalDateTime.now().minusDays(5).withNano(0),
        LocalDateTime.now().plusDays(5).withNano(0)),
        new Blog(3, "Forever in Space", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
        + "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
        status2, user2.getId(), 2, new ArrayList<>(), new ArrayList<>(), LocalDateTime.now().minusDays(3).withNano(0),
        LocalDateTime.now().plusMonths(1).withNano(0))

    };

    Blog blog1 = testBlogs[0];
    Blog blog2 = testBlogs[1];
    Blog blog3 = testBlogs[2];

    private Comment[] testComments = {
        new Comment(1, 1, blog1.getId(), "This is great!!", LocalDateTime.now().minusMonths(4).withNano(0)),
        new Comment(2, 2, blog2.getId(), "I don't believe that is true", LocalDateTime.now().minusMinutes(190).withNano(0)),
        new Comment(3, 3, blog2.getId(), "Awesome!", LocalDateTime.now().minusMinutes(90).withNano(0)),
        new Comment(4, 1, blog3.getId(), "WOOWWWw", LocalDateTime.now().minusDays(90).withNano(0)),};

    @Before
    public void setUp() {
        JdbcTemplate heySQL = spaceDao.getHeySql();
        heySQL.execute("DELETE FROM BlogHash WHERE id > 0");
        heySQL.execute("DELETE FROM BlogIMG WHERE id > 0");
        heySQL.execute("DELETE FROM BlogCat WHERE id > 0");
        heySQL.execute("DELETE FROM Categories WHERE id > 0");
        heySQL.execute("DELETE FROM ImageURLs WHERE id > 0");
        heySQL.execute("DELETE FROM Comments WHERE id > 0");
        heySQL.execute("DELETE FROM Hashtags WHERE id > 0");
        heySQL.execute("DELETE FROM Blogs WHERE id > 0");
        heySQL.execute("DELETE FROM Statuses WHERE id > 0");
        heySQL.execute("DELETE FROM Users WHERE id > 0");

        String ADD_STATUS = "INSERT INTO Statuses(id,name) VALUES(?,?)";
        for (Status status : testStatuses) {
            heySQL.update(ADD_STATUS, status.getId(), status.getName());
        }
        
        String ADD_USER = "INSERT INTO Users(id,displayName,UserName,Password,Email) VALUES(?,?,?,?,?)";
        for (User user : testUsers) {
            heySQL.update(ADD_USER, user.getId(), user.getDisplayName(), user.getUsername(), user.getPassword(), user.getEmail());
        }
        
        String ADD_BLOG = "INSERT INTO Blogs(id,userid, statusid,title, body,postedtime,expirationtime) VALUES(?,?,?,?,?,?,?)";
        for (Blog blog : testBlogs) {
            heySQL.update(ADD_BLOG, blog.getId(), blog.getUserId(), blog.getStatus().getId(), blog.getTitle(), blog.getBody(), blog.getDatePosted(), blog.getPostExpired());
        }

        String ADD_COMMENT = "INSERT INTO comments(id,userid,blogid,body,postedtime) VALUES(?,?,?,?,?)";
        for (Comment comment : testComments) {
            heySQL.update(ADD_COMMENT, comment.getId(), comment.getUserId(), comment.getBlogId(), comment.getBody(), comment.getPostedTime());
        }

        String ADD_HASHTAG = "INSERT INTO hashtags(id, name) VALUES(?,?)";
        for (Hashtag hashtag : testHashtags) {
            heySQL.update(ADD_HASHTAG, hashtag.getId(), hashtag.getName());
        }
        
        String ADD_CATEGORY = "INSERT INTO categories(id,name) VALUES(?,?)";
        for (Category category : testCategories) {
            heySQL.update(ADD_CATEGORY, category.getId(), category.getName());
        }
        
        String ADD_IMAGE = "INSERT INTO imageurls (Id, URL, Type) VALUES (?, ?, ?)";
        for (Image image : testImages) {
            heySQL.update(ADD_IMAGE, image.getId(), image.getURL(), image.getType());
        }
        
        blog1.addHashtag(hashtag1);
        blog1.addHashtag(hashtag2);
        blog1.addHashtag(hashtag3);
        blog2.addHashtag(hashtag1);
        blog2.addHashtag(hashtag4);
        blog2.addHashtag(hashtag5);

        String INSERT_INTO_BLOGHASH = "INSERT INTO BlogHash (id, Blogid, Hashtagid) VALUES (?,?,?)";
        heySQL.update(INSERT_INTO_BLOGHASH, 1, 1, 1);
        heySQL.update(INSERT_INTO_BLOGHASH, 2, 1, 2);
        heySQL.update(INSERT_INTO_BLOGHASH, 3, 1, 3);
        heySQL.update(INSERT_INTO_BLOGHASH, 4, 2, 1);
        heySQL.update(INSERT_INTO_BLOGHASH, 5, 2, 4);
        heySQL.update(INSERT_INTO_BLOGHASH, 6, 2, 5);

        String INSERT_INTO_BLOGCAT = "INSERT INTO BlogCat(id, Blogid, CatId) VALUES (?,?,?)";
        heySQL.update(INSERT_INTO_BLOGCAT, 1, 1, 1);
        heySQL.update(INSERT_INTO_BLOGCAT, 2, 1, 2);
        heySQL.update(INSERT_INTO_BLOGCAT, 3, 2, 2);

    }

    @Test
    public void testAddAndGetComment() {

        Comment comment = testComments[0];

        spaceDao.addComment(comment);
        List<Comment> allComments = spaceDao.getAllComments();

        assertNotNull("List should never be null", allComments);
        assertEquals("should only have 4 commentes in list",5 , allComments.size());
        Comment testcomment= spaceDao.getComment(comment.getId());
        assertEquals("should equal each other", comment,testcomment );

    }

    @Test
    public void testGetAndUpdateComment() {


        Comment comment = testComments[1];



        List<Comment> allComments = spaceDao.getAllComments();
        assertTrue("List should contain comment", allComments.contains(comment));

        Comment toEdit = spaceDao.getComment(comment.getId());
        assertEquals("they should equal each other", comment, toEdit);
        //get organization out and edit it


        comment.setBody("I do not agree with this");
        assertNotEquals("comment should not equal each other after changes", comment, toEdit);
        spaceDao.updateComment(comment);

        assertNotEquals(comment, toEdit);

        toEdit = spaceDao.getComment(comment.getId());

        assertEquals("after updating comment they should equal each other", comment, toEdit);
    }

    @Test
    public void testRemoveComment() {
        Comment comment = testComments[1];
        Comment comment2 = testComments[0];
        spaceDao.addComment(comment);
        spaceDao.addComment(comment2);

        List<Comment> allComments = spaceDao.getAllComments();
        assertTrue("list should be contain both comments",allComments.contains(comment));
        assertTrue("list should be contain both comments",allComments.contains(comment2));

        Comment fromDao = spaceDao.getComment(comment.getId());

        assertEquals("both objects should equal each other",comment, fromDao);

        spaceDao.removeComment(comment.getId());
        allComments = spaceDao.getAllComments();

        assertEquals("list should be contain only comment2",5,allComments.size());
        assertTrue("list should be contain  comment2",allComments.contains(comment2));

    }
}
