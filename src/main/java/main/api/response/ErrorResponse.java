package main.api.response;

import java.util.Map;


public class ErrorResponse extends Result {

    private Map<String, String> errors;

    public ErrorResponse(boolean result, Map<String, String> errors) {
        super(result);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}


