package com.tsg.spacestation.ops;

import com.tsg.spacestation.dao.SpaceDaoImpl;
import com.tsg.spacestation.dto.Blog;
import com.tsg.spacestation.dto.Category;
import com.tsg.spacestation.dto.Hashtag;
import com.tsg.spacestation.dto.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/spaceblog")
public class SpaceController {

    @Autowired
    SpaceDaoImpl spaceDao;

    @GetMapping({"/home", "/", "/main", ""})
    public String mainPage(Model model) {
        List<Category> categories = spaceDao.getAllCategories();

        model.addAttribute("categories", categories);
        return "index";
    }

    @GetMapping("/category/id")
    public String blogCategory(Model model, HttpServletRequest request) {

        int id = Integer.parseInt(request.getParameter("id"));
        Category category = spaceDao.getCategory(id);
        List<Blog> allBlogs = spaceDao.getAllBlogsForCategoryThatAreApproved(id);
        List<Blog> blogs = new ArrayList();
        for (Blog aBlog : allBlogs) {
            if (aBlog.getPostExpired().isAfter(LocalDateTime.now())
                    && aBlog.getDatePosted().isBefore(LocalDateTime.now())) {
                blogs.add(aBlog);
            }
        }

        for (Blog i : blogs) {
            String text = i.getBody().substring(0, i.getBody().length() > 300 ? 300 : i.getBody().length() - 1);

            i.setBody(text);
        }
        List<Category> categories = spaceDao.getAllCategories();

        model.addAttribute("categories", categories);

        model.addAttribute("category", category);
        model.addAttribute("blogs", blogs);
        return "category";
    }

    @GetMapping("/blog/id")
    public String blogPage(Model model, HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        Blog blog = spaceDao.getBlog(id);

        List<Category> categories = spaceDao.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("blog", blog);
        return "blog";
    }

    @GetMapping("/login")
    public String login(Model model) {

        return "login";
    }

//    @GetMapping("/page/{id}")
//    public String page(Model model, HttpServletRequest request) {
//        List<Category> categories = spaceDao.getAllCategories();
//        model.addAttribute("categories", categories);
//        int id = Integer.parseInt(request.getParameter("id"));
//        Category category = spaceDao.getCategory(id);
//
//        return "page";
//    }

//    @GetMapping("/editpage/{name}")
//    public String editPage(Model model, @PathVariable String name) {
//
//        return "editpage";
//    }

    @GetMapping("/post/new")
    public String createPost(Model model, HttpServletRequest request) {
        model.addAttribute("currentTime", LocalDateTime.now().withNano(0).withSecond(0));

        List<Category> categories = spaceDao.getAllCategories();
        model.addAttribute("categories", categories);

        int id = Integer.parseInt(request.getParameter("id"));
        Category category = spaceDao.getCategory(id);
        model.addAttribute("category", category);

//        List<Hashtag> hashtags = spaceDao.getAllHashtags();
//        model.addAttribute("hashtags", hashtags);
//
//        List<Image> images = spaceDao.getAllImages();
//        model.addAttribute("images", images);
        return "createpost";
    }

    @PostMapping("/post/new/createPost")
    public String performCreatePost(HttpServletRequest request, Model model) {
        Blog blog = new Blog();

        blog.setCategoryId(Integer.parseInt(request.getParameter("catId")));
        // data from title
        blog.setTitle(request.getParameter("title"));
        // data from tiny
        blog.setBody(request.getParameter("body"));
        // status should be set to staged
        blog.setStatus(spaceDao.getStatus(2));
        // date posted set to time to post
        blog.setDatePosted(LocalDateTime.parse(request.getParameter("datePosted"), DateTimeFormatter.ISO_DATE_TIME));
        // date expired
        blog.setPostExpired(LocalDateTime.parse(request.getParameter("postExpiration"), DateTimeFormatter.ISO_DATE_TIME));

        String splitHashtag[] = request.getParameter("hashtags").split(",");
        List<String> stringTagsAsList = Arrays.asList(splitHashtag);
        List<Hashtag> allTags = spaceDao.getAllHashtags();
        List<Hashtag> tagsToAdd = new ArrayList();

        // Find list of tags to add
        List<String> allTagsAsStrings = allTags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());

        for (String tagToCompare : stringTagsAsList) {
            if (!allTagsAsStrings.contains(tagToCompare)) {
                Hashtag hash = new Hashtag();
                hash.setName(tagToCompare);
                tagsToAdd.add(hash);
            }
        }

        for (Hashtag hashtag : tagsToAdd) {
            spaceDao.addHashtag(hashtag);
        }

        // Refetch all hashtags
        Map<String, Integer> hashTagIdMap = spaceDao.getAllHashtags().stream()
                .collect(Collectors.toMap(Hashtag::getName, Hashtag::getId));

        // Map ids for bridge table
        List<Hashtag> newBlogHashes = stringTagsAsList.stream()
                .map(i -> {
                    Hashtag hashTagWithId = new Hashtag();
                    hashTagWithId.setName(i);
                    hashTagWithId.setId(hashTagIdMap.get(i));
                    return hashTagWithId;
                }).collect(Collectors.toList());

        // Insert the new blog hashes
        blog.setHashtags(newBlogHashes);

//TODO: Temporary property. Remove when mapped with actual params.
        blog.setUserId(-1);
        spaceDao.addBlog(blog);

        return "redirect:/spaceblog/blog/review";
    }

    @GetMapping("/blog/review")
    public String blogForReview(Model model) {
        // access restricted by user
        List<Category> categories = spaceDao.getAllCategories();
        model.addAttribute("categories", categories);

        List<Blog> blogs = spaceDao.getAllBlogs();
        model.addAttribute("blogs", blogs);

        return "blogreview";
    }

    @GetMapping("/blog/approve/{id}")
    public String blogApproved(Model model, @PathVariable Integer id) {
        Blog blog = spaceDao.getBlog(id);
        Status approvedStatus = spaceDao.getStatus(1);
        if (!blog.getStatus().equals(approvedStatus)) {
            blog.setStatus(approvedStatus);
        } else {
            blog.setStatus(spaceDao.getStatus(2));
        }
        spaceDao.updateBlog(blog);
        return "redirect:/spaceblog/blog/review";
    }

    @GetMapping("/blog/delete/{id}")
    public String blogDelete(Model model, @PathVariable Integer id) {
        spaceDao.removeBlog(id);
        return "redirect:/spaceblog/blog/review";
    }

    @GetMapping("/blog/edit")
    public String blogEdit(HttpServletRequest request, Model model) {
        model.addAttribute("currentTime", LocalDateTime.now().withNano(0).withSecond(0));

        List<Category> categories = spaceDao.getAllCategories();
        model.addAttribute("categories", categories);

        Blog blog = spaceDao.getBlog(Integer.parseInt(request.getParameter("id")));
        model.addAttribute("blog", blog);

        Category category = spaceDao.getCategory(blog.getCategoryId());
        model.addAttribute("category", category);

        List<Hashtag> allTags = blog.getHashtags();
        String tagsAsString = "";
        for (Hashtag aTag : allTags) {
            tagsAsString += aTag.getName() + ",";
        }
        model.addAttribute("tagsAsString", tagsAsString);

        return "blogedit";
    }

    @PostMapping("/blog/edit/editBlog")
    public String performBlogEdit(HttpServletRequest request, Model model) {
//        Blog blog = new Blog();

        Blog blog = spaceDao.getBlog(Integer.parseInt(request.getParameter("id")));

        //blog.setCategoryId(Integer.parseInt(request.getParameter("catId")));
        // data from title
        blog.setTitle(request.getParameter("title"));
        // data from tiny
        blog.setBody(request.getParameter("body"));
        // status should be set to staged
        // blog.setStatus(spaceDao.getStatus(2));
        // date posted set to local time now
        blog.setDatePosted(LocalDateTime.parse(request.getParameter("datePosted"), DateTimeFormatter.ISO_DATE_TIME));
        // date expired
        blog.setPostExpired(LocalDateTime.parse(request.getParameter("postExpiration"), DateTimeFormatter.ISO_DATE_TIME));

        String splitHashtag[] = request.getParameter("hashtags").split(",");
        List<String> stringTagsAsList = Arrays.asList(splitHashtag);
        List<Hashtag> allTags = spaceDao.getAllHashtags();
        List<Hashtag> tagsToAdd = new ArrayList();

        // Find list of tags to add
        List<String> allTagsAsStrings = allTags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());

        for (String tagToCompare : stringTagsAsList) {
            if (!allTagsAsStrings.contains(tagToCompare)) {
                Hashtag hash = new Hashtag();
                hash.setName(tagToCompare);
                tagsToAdd.add(hash);
            }
        }

        for (Hashtag hashtag : tagsToAdd) {
            spaceDao.addHashtag(hashtag);
        }

        // Refetch all hashtags
        Map<String, Integer> hashTagIdMap = spaceDao.getAllHashtags().stream()
                .collect(Collectors.toMap(Hashtag::getName, Hashtag::getId));

        // Map ids for bridge table
        List<Hashtag> newBlogHashes = stringTagsAsList.stream()
                .map(i -> {
                    Hashtag hashTagWithId = new Hashtag();
                    hashTagWithId.setName(i);
                    hashTagWithId.setId(hashTagIdMap.get(i));
                    return hashTagWithId;
                }).collect(Collectors.toList());

        // Insert the new blog hashes
        blog.setHashtags(newBlogHashes);

//TODO: Temporary property. Remove when mapped with actual params.
        // blog.setUserId(-1);
        spaceDao.updateBlog(blog);

        return "redirect:/spaceblog/blog/review";
    }

}
