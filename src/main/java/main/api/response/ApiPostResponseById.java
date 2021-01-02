package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.model.Tag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiPostResponseById {

    private long timestamp;
    private boolean active;
    private UserIdNameResponse user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<Comment> comments = new ArrayList<>();
    private List<String> tags;

    public ApiPostResponseById(long timestamp, boolean isActive, int userId, String userName, String title, String text,
                                int likeCount, int dislikeCount, int viewCount, List<String> tags){

        this.timestamp = timestamp;
        active = isActive;
        user = new UserIdNameResponse(userId,userName);
        this.title = title;
        this.text = text;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.viewCount = viewCount;
        this.tags = tags;



    }

    public void addComment(int commentId, long commentTimestamp, String commentText, int commentUserId, String commentUserName,
                           String userPhoto){
        CommentUserResponse commentUserResponse = new CommentUserResponse(commentUserId, commentUserName, userPhoto);
        Comment comment = new Comment(commentId, commentTimestamp, commentText, commentUserResponse);
        comments.add(comment);
    }


    @Data
    @AllArgsConstructor
    class Comment{

        private int id;
        private long timestamp;
        private String text;
        private CommentUserResponse userResponse;

    }

    @Data
    @AllArgsConstructor
    class CommentUserResponse{
        private int id;
        private String name;
        private String photo;
    }

    @Data
    @AllArgsConstructor
    class UserIdNameResponse{

        private int id;
        private String name;
    }





}
