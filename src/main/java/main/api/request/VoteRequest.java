package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class VoteRequest {

    @JsonProperty("post_id")
    private int postId;

}
