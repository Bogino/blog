package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ApiTagResponse {


    private ArrayList<TagResponse> tags = new ArrayList<>();

    public void addTag(String tagName, double tagWeight) {

        TagResponse tagResponse = new TagResponse(tagName, tagWeight);
        tags.add(tagResponse);
    }


    @Data
    @AllArgsConstructor
    class TagResponse {

        private String name;
        private double weight;


    }


}
