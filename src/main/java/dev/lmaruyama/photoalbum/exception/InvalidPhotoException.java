package dev.lmaruyama.photoalbum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPhotoException extends RuntimeException {

    public InvalidPhotoException() {
        super("Invalid photo, please try again");
    }
}
