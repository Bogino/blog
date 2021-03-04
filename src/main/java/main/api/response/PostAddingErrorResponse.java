package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class PostAddingErrorResponse {

    private boolean result;
    private HashMap<String, String> errors;
}
