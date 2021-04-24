package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class AddCommentRequest {


    @JsonProperty("parent_id")
    private int parentId;

    private String text;

    @JsonProperty("post_id")
    private int postId;

}
