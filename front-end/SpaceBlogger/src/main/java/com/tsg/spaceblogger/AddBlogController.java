/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tsg.spaceblogger;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author djSublett
 */
@Controller
public class AddBlogController {
    
    @Autowired SpaceDaoImpl spaceBlog;
    
    @PostMapping("/addBlog")
    public String addBlog(HttpServletRequest request){
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String spaceBlog = request.getParameter("spaceBlog");
        String category = request.getParameter("category");
        String postDate = request.getParameter("postDate");
        String expDate = request.getParameter("expDate");
        
        
        return "redirect:/addBlog.html";
    }
}
