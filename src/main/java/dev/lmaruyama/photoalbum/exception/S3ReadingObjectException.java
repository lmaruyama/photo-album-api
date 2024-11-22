package dev.lmaruyama.photoalbum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class S3ReadingObjectException extends RuntimeException {
    public S3ReadingObjectException(String message) {
        super(message);
    }
}
