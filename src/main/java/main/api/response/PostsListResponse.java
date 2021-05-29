package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import main.model.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class PostsListResponse {

    private long count;
    private List<PostResponse> posts;

    public PostsListResponse() {
        posts = new ArrayList<>();
    }


    @Data
    @AllArgsConstructor
    public static class PostResponse {

        private int id;
        private long timestamp;
        @JsonProperty("user")
        private UserIdNameResponse userIdNameResponse;
        private String title;
        private String announce;
        private long likeCount;
        private long dislikeCount;
        private int commentCount;
        private int viewCount;

        public PostResponse(Post post) {
            id = post.getId();
            timestamp = post.getTime().getTime();
            userIdNameResponse = new UserIdNameResponse(post.getUserId().getId(), post.getUserId().getName());
            title = post.getTitle();
            announce = post.getText();
            likeCount =post.getVotes().stream().filter(v -> v.getValue() > 0).count();;
            dislikeCount =post.getVotes().stream().filter(v -> v.getValue() < 0).count();;
            commentCount = post.getComments().size();
            viewCount = post.getViewCount();
        }

    }

    @Data
    @AllArgsConstructor
    static class UserIdNameResponse {

        private int id;
        private String name;
    }
}
