package com.tsg.spacestation.ops;


import com.tsg.spacestation.dao.SpaceDaoImpl;
import com.tsg.spacestation.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/brandon")
public class BrandonController {



    @Autowired
    SpaceDaoImpl spaceDao;

    @PostMapping("/post/new")
    public String performCreatePost (Model model, HttpServletRequest request){
        Blog blog = new Blog();

        String title = request.getParameter("title");
        blog.setTitle(title);

        String body = request.getParameter("body");
        blog.setBody(body);

        String statusId = request.getParameter("statusId");
        blog.setStatus(spaceDao.getStatus(Integer.parseInt(statusId)));

        String categoryId = request.getParameter("categoryId");
        blog.setCategoryId(Integer.parseInt(categoryId));

        String datePosted = request.getParameter("datePosted");
        LocalDateTime postDate = LocalDateTime.parse(datePosted, DateTimeFormatter.ISO_DATE_TIME);
        blog.setDatePosted(postDate);

        String postExpired = request.getParameter("postExpired");
        LocalDateTime expireTime = LocalDateTime.parse(postExpired, DateTimeFormatter.ISO_DATE_TIME);
        blog.setPostExpired(expireTime);

        // probably needs refactoring after create page is tweaked
        String userId = request.getParameter("userId");
        blog.setUserId(Integer.parseInt(userId));

        // probably needs refactoring after create page is tweaked
        String[] hashtagIds = request.getParameterValues("hashtagId");
        List<Hashtag> hashtags = new ArrayList<>();
        if (hashtagIds != null) {
            for (String hashtagId : hashtagIds) {
                hashtags.add(spaceDao.getHashtag(Integer.parseInt(hashtagId)));
            }
        }
        blog.setHashtags(hashtags);

        // probably needs refactoring after create page is tweaked
        String[] imageIds = request.getParameterValues("imageId");
        List<Image> images = new ArrayList<>();
        if (imageIds != null) {
            for (String imageId : imageIds) {
                images.add(spaceDao.getImage(Integer.parseInt(imageId)));
            }
        }
        blog.setImages(images);

        return "redirect:/createpost";
    }
//    <input type="file" name="pic" accept="image/*">

    @GetMapping("/post/new")
    public String createPost(Model model, HttpServletRequest request) {
        List<Category> categories = spaceDao.getAllCategories();
        model.addAttribute("categories", categories);

        List<Status> statuses = spaceDao.getAllStatuses();
        model.addAttribute("statuses", statuses);

        List<Hashtag> hashtags = spaceDao.getAllHashtags();
        model.addAttribute("hashtags", hashtags);

        List<Image> images = spaceDao.getAllImages();
        model.addAttribute("images", images);

        return "createpost";
    }
}
