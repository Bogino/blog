package main.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AddPostRequest {

    private long timestamp;
    private int active;
    private String title;
    private List<String> tags;
    private String text;
}
