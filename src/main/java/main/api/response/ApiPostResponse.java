package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiPostResponse {

    private long count;
    private List<PostResponse> posts;

    public ApiPostResponse(){
        posts = new ArrayList<>();
    }

    public void setPosts (long count, int postId, Date timestamp, int userId, String userName, String title, String announce, int likeCount, int dislikeCount, int commentCount, int viewCount) {

        this.count = count;
        UserIdNameResponse user = new UserIdNameResponse(userId, userName);
        PostResponse postResponse = new PostResponse(postId, timestamp, user, title, announce, likeCount, dislikeCount, commentCount, viewCount);
        posts.add(postResponse);

    }
    public void sortPostsByLikes(){
        posts.sort(new LikeComparator());
    }

    public void sortPostsByCommentCount(){
        posts.sort(new CommentComparator());
    }

    @Data
    @AllArgsConstructor
    class PostResponse {

        private int id;
        private Date timestamp;
        private UserIdNameResponse userIdNameResponse;
        private String title;
        private String announce;
        private int likeCount;
        private int dislikeCount;
        private int commentCount;
        private int viewCount;

    }

    @Data
    @AllArgsConstructor
    class UserIdNameResponse {

        private int id;
        private String name;
    }
}
