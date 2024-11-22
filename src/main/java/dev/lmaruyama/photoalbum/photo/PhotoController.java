package dev.lmaruyama.photoalbum.photo;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/photos")
class PhotoController {

    private final PhotoService photoService;

    PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/album/{albumId}")
    List<Photo> retrievePhotos(@PathVariable("albumId") Long albumId) {
        return photoService.retrievePhotos(albumId);
    }

    @GetMapping("/{id}")
    Photo retrievePhoto(@PathVariable("id") Long id) {
        return photoService.retrievePhoto(id);
    }

    @GetMapping(value = "/{id}/image", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    byte[] retrieveImage(@PathVariable("id") Long id) {
        return photoService.retrieveImage(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/album/{albumId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Photo addPhotoToAlbum(@PathVariable Long albumId, @RequestParam("description") String description, @NotNull @RequestParam("picture") MultipartFile picture) {
        return photoService.addPhotoToAlbum(albumId, description, picture);
    }

    @PatchMapping("/{id}")
    void updatePhotoDetails(@PathVariable("id") Long id, @RequestBody Photo photo) {
        photoService.updatePhotoDetails(id, photo);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deletePhoto(@PathVariable("id") Long id) {
        photoService.deletePhoto(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/album/{albumId}")
    void deletePhotosFromAlbum(@PathVariable("albumId") Long albumId) {
        photoService.deletePhotosFromAlbum(albumId);
    }
}
