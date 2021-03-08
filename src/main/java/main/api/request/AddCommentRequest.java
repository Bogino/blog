package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddCommentRequest {

    @JsonProperty("parent_id")
    int parentId;

    @JsonProperty("post_id")
    int postId;

    String text;

}
