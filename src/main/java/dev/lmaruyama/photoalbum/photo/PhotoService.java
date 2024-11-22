package dev.lmaruyama.photoalbum.photo;

import dev.lmaruyama.photoalbum.exception.InvalidPhotoException;
import dev.lmaruyama.photoalbum.exception.PhotoAlbumNotFoundException;
import dev.lmaruyama.photoalbum.exception.PhotoNotFoundException;
import dev.lmaruyama.photoalbum.s3.S3Buckets;
import dev.lmaruyama.photoalbum.s3.S3Service;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoService.class);

    private final PhotoRepository photoRepository;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public PhotoService(PhotoRepository photoRepository, S3Service s3Service, S3Buckets s3Buckets) {
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
        this.photoRepository = photoRepository;
    }

    public List<Photo> retrievePhotos(Long albumId) {
        return photoRepository.findByAlbumId(albumId).orElseThrow(() -> new PhotoAlbumNotFoundException(albumId));
    }

    public byte[] retrieveImage(Long id) {
        final Photo photo = photoRepository.findById(id).orElseThrow(() -> new PhotoNotFoundException(id));
        final String path = "%s/%s".formatted(photo.getAlbumId(), photo.getImageKey());
        return s3Service.downloadObject(s3Buckets.getPhotoAlbum(), path);
    }

    public Photo addPhotoToAlbum(Long albumId, String description, @NotNull MultipartFile picture) {
        try {
            final String key = UUID.randomUUID().toString();
            final String path = "%s/%s".formatted(albumId, key);
            s3Service.uploadObject(s3Buckets.getPhotoAlbum(), path, picture.getBytes());
            Photo photo = new Photo(
                    key,
                    description,
                    albumId
            );
            return photoRepository.save(photo);
        } catch (IOException e) {
            final String errorMessage =
                    "Something went wrong while uploading a new photo. Error: %s".formatted(e.getMessage());
            LOGGER.error(errorMessage, e);
            throw new InvalidPhotoException();
        }
    }

    public Photo retrievePhoto(Long id) {
        return photoRepository.findById(id).orElseThrow(() -> new PhotoNotFoundException(id));
    }

    public void updatePhotoDetails(Long id, Photo photo) {
        Photo original = retrievePhoto(id);
        original.setDescription(photo.getDescription());
        photoRepository.save(original);
    }

    public void deletePhoto(Long id) {
        delete(retrievePhoto(id));
    }

    private void delete(Photo photo) {
        final String path = "%s/%s".formatted(String.valueOf(photo.getAlbumId()), photo.getImageKey());
        s3Service.deleteObject(s3Buckets.getPhotoAlbum(), path);
        photoRepository.delete(photo);
    }

    public void deletePhotosFromAlbum(Long albumId) {
        final List<Photo> photos = retrievePhotos(albumId);
        photos.stream().parallel().forEach(this::delete);
    }
}
