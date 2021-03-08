package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@AllArgsConstructor
@Data
public class ErrorResponse {

    private boolean result;
    private HashMap<String, String> errors;
}


