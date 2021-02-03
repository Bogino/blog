package main.api.response;

import lombok.Data;

import java.util.TreeMap;
import java.util.TreeSet;

@Data
public class ApiCalendarResponse {

    private TreeSet<Integer> years;
    private TreeMap<String, Integer> posts;

    public ApiCalendarResponse() {
        years = new TreeSet<>();
        posts = new TreeMap<>();
    }

}
