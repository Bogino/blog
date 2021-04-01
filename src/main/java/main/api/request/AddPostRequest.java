package main.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddPostRequest {

    private long timestamp;
    private int active;
    private String title;
    private ArrayList<String> tags;
    private String text;
}
