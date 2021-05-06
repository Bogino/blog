package main.exception;


import main.api.response.ErrorResponse;
import main.api.response.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ErrorResponse handleMaxSizeException() {

        ErrorResponse imageUploadErrorResponse = new ErrorResponse(false, new HashMap<>());
        imageUploadErrorResponse.getErrors().put("image", "Размер файла превышает допустимый размер");
        return imageUploadErrorResponse;
    }


    @ExceptionHandler(NullPointerCommentTextException.class)
    public final ResponseEntity<ErrorResponse> handleNullPointerCommentTextException(){

        ErrorResponse response = new ErrorResponse(false, new HashMap<>());

        response.getErrors().put("text", "Текст не задан");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<Result> handleUsernameNotFoundException(UsernameNotFoundException e){

        e.getMessage();

        return new ResponseEntity<>(new Result(false), HttpStatus.OK);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public final void handlePostNotFoundException(PostNotFoundException e) {
        e.getMessage();
    }

}
