package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PostModerationRequest {

    public PostModerationRequest(){
        super();
    }

    @JsonProperty("post_id")
    private int postId;

    private String decision;
}
