package com.tsg.spacestation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog {
   private int id;
   private String title;
   private String body;
   private Status status;
   private int userId;
   private int categoryId;
   private List<Hashtag> hashtags  = new ArrayList();
   private List<Image> images  = new ArrayList();
   private LocalDateTime datePosted;
   private LocalDateTime postExpired;


   public void addHashtag(Hashtag hashtag){
      this.hashtags.add(hashtag);
   }
   public void addStatus(Status status){
      this.status = status;
   }

   public void addImage(Image image){
      this.images.add(image);
   }
}


