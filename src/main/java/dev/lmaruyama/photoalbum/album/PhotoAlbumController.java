package dev.lmaruyama.photoalbum.album;

import dev.lmaruyama.photoalbum.photo.PhotoService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/photo-albums")
class PhotoAlbumController {

    private final PhotoAlbumService service;

    public PhotoAlbumController(PhotoAlbumService service) {
        this.service = service;
    }

    @GetMapping
    List<PhotoAlbum> retrieveAllPhotoAlbums() {
        return service.retrieveAllPhotoAlbums();
    }

    @GetMapping("/{id}")
    PhotoAlbum retrievePhotoAlbum(@PathVariable long id) {
        return service.retrievePhotoAlbum(id);
    }

    @PostMapping
    ResponseEntity<PhotoAlbum> createPhotoAlbum(@RequestBody @Valid PhotoAlbum photoAlbum) {
        final PhotoAlbum newPhotoAlbum = service.createPhotoAlbum(photoAlbum);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newPhotoAlbum.getId())
                .toUri();

        return ResponseEntity.created(location).body(newPhotoAlbum);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updatePhotoAlbum(@PathVariable long id, @RequestBody @Valid PhotoAlbum photoAlbum) {
        service.updatePhotoAlbum(id, photoAlbum);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePhotoAlbum(@PathVariable long id) {
        service.deleteAlbumById(id);
        return ResponseEntity.noContent().build();
    }
}
