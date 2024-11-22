package dev.lmaruyama.photoalbum.album;

import dev.lmaruyama.photoalbum.exception.PhotoAlbumNotFoundException;
import dev.lmaruyama.photoalbum.photo.PhotoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoAlbumService {

    private final PhotoAlbumRepository repository;
    private final PhotoService photoService;

    public PhotoAlbumService(PhotoAlbumRepository repository, PhotoService photoService) {
        this.repository = repository;
        this.photoService = photoService;
    }

    public List<PhotoAlbum> retrieveAllPhotoAlbums() {
        return repository.findAll();
    }

    public PhotoAlbum retrievePhotoAlbum(long id) {
        return repository.findById(id).orElseThrow(() -> new PhotoAlbumNotFoundException(id));
    }

    public PhotoAlbum createPhotoAlbum(PhotoAlbum photoAlbum) {
        return repository.save(photoAlbum);
    }

    public void updatePhotoAlbum(long id, @Valid PhotoAlbum photoAlbum) {
        final PhotoAlbum current = repository.findById(id).orElseThrow(() -> new PhotoAlbumNotFoundException(id));

        PhotoAlbum updated = new PhotoAlbum(
                current.getId(),
                photoAlbum.getTitle(),
                photoAlbum.getLocation(),
                photoAlbum.getDescription(),
                photoAlbum.getEventDate()
        );

        repository.save(updated);
    }

    public void deleteAlbumById(Long id) {
        photoService.deletePhotosFromAlbum(id);
        repository.deleteById(id);
    }
}
