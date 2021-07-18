package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.model.Post;
import main.model.Tag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ApiPostResponseById {

    private int id;
    private long timestamp;
    private boolean active = true;
    private UserIdNameResponse user;
    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;
    private int viewCount;
    private Set<Comment> comments = new HashSet<>();
    private Set<String> tags;

    public ApiPostResponseById(Post post){

        id = post.getId();
        timestamp = post.getTime().toEpochSecond(ZoneOffset.UTC);
        user = new UserIdNameResponse(post.getUserId().getId(),post.getUserId().getName());
        title = post.getTitle();
        text = post.getText();
        likeCount =post.getVotes().stream().filter(v -> v.getValue() > 0).count();
        dislikeCount =post.getVotes().stream().filter(v -> v.getValue() < 0).count();
        viewCount = post.getViewCount();
        tags = post.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
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
        private CommentUserResponse user;

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
