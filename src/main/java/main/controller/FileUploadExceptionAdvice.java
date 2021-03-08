package main.controller;

import main.api.response.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;

@ControllerAdvice
public class FileUploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ErrorResponse handleMaxSizeException() {

        ErrorResponse imageUploadErrorResponse = new ErrorResponse(false, new HashMap<>());
        imageUploadErrorResponse.getErrors().put("image", "Размер файла превышает допустимый размер");
        return imageUploadErrorResponse;
    }

}
