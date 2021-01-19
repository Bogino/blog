package main.api.response;

import lombok.Data;

import java.util.HashMap;

@Data
public class RegistrationErrorResponse {

    private final boolean result = false;
    private HashMap<String, String> errors = new HashMap<>();

}
