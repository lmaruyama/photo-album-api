package dev.lmaruyama.photoalbum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PhotoAlbumNotFoundException extends RuntimeException {
    public PhotoAlbumNotFoundException(long id) {
        super("Photo Album with id %s was not found".formatted(id));
    }
}
