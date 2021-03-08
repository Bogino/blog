package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;


public class ErrorResponse extends Result{

    private boolean result;
    private HashMap<String, String> errors;

    public ErrorResponse(boolean result, HashMap<String, String> errors) {
        super(result);
        this.errors = errors;
    }

    public HashMap<String, String> getErrors(){
        return errors;
    }
}


