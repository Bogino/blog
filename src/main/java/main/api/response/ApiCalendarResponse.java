package main.api.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

@Data
public class ApiCalendarResponse {

    TreeSet<Integer> years;
    TreeMap<String, Integer> posts;

    public ApiCalendarResponse() {
        years = new TreeSet<>();
        posts = new TreeMap<>();
    }

}
