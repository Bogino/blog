package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class ApiTagResponse {


    private Set<TagResponse> tags = new HashSet<>();

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
