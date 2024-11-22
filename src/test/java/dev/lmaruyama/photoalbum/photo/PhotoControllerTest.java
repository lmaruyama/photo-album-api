package dev.lmaruyama.photoalbum.photo;

import dev.lmaruyama.photoalbum.exception.PhotoAlbumNotFoundException;
import dev.lmaruyama.photoalbum.exception.PhotoNotFoundException;
import dev.lmaruyama.photoalbum.s3.S3Buckets;
import dev.lmaruyama.photoalbum.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({PhotoController.class, PhotoService.class})
@AutoConfigureMockMvc
class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoRepository photoRepository;

    @MockBean
    private S3Service s3Service;

    @MockBean
    private S3Buckets s3Buckets;

    private List<Photo> photos = new ArrayList<>();

    @BeforeEach
    void setUp() {

        photos = List.of(
                new Photo(1L, "123-456-678-910", "Sunset", 1L),
                new Photo(2L, "109-876-543-321", "Cityscape", 1L)
        );
    }

    @Test
    void shouldReturnAllPhotos() throws Exception {
        when(photoRepository.findByAlbumId(1L)).thenReturn(Optional.of(photos));

        var content = """
            [
              {
                "id": 1,
                "imageKey": "123-456-678-910",
                "description": "Sunset",
                "albumId": 1
              },
              {
                "id": 2,
                "imageKey": "109-876-543-321",
                "description": "Cityscape",
                "albumId": 1
              }
            ]
        """;
        mockMvc
                .perform(get("/api/v1/photos/album/1"))
                .andExpect(content().json(content))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundIfAlbumIdIsInvalid() throws Exception {
        when(photoRepository.findByAlbumId(999L)).thenThrow(PhotoAlbumNotFoundException.class);

        mockMvc
                .perform(get("/api/v1/photos/album/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAPhoto() throws Exception {
        var content = """
              {
                "id": 1,
                "imageKey": "123-456-678-910",
                "description": "Sunset",
                "albumId": 1
              }
          """;

        when(photoRepository.findById(1L)).thenReturn(Optional.of(photos.get(0)));

        mockMvc
                .perform(get("/api/v1/photos/1"))
                .andExpect(content().json(content))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenThePhotoIdIsInvalid() throws Exception {
        when(photoRepository.findById(999L)).thenThrow(PhotoNotFoundException.class);
        mockMvc
                .perform(get("/api/v1/photos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddPhotoToAlbum() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "picture",
                "sunset.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image-content".getBytes()
        );

        Photo photo = new Photo(
                3L, "123-123-123-123", "Sunset 2", 1L
        );

        final String key = "%s/%s".formatted("1", "123-123-123-123");
        when(s3Buckets.getPhotoAlbum()).thenReturn("photo-album");
        when(photoRepository.save(photo)).thenReturn(photo);
        doNothing().when(s3Service).uploadObject("photo-album", key, file.getBytes());

        mockMvc.perform(
                multipart("/api/v1/photos/album/1")
                        .file(file)
                        .param("description", "Sunset 2")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andExpect(status().isCreated());
    }

    @Test
    void shouldReturnTheImage() throws Exception {
        final Photo photo = photos.get(0);
        final String key = "%s/%s".formatted(photo.getAlbumId(), photo.getImageKey());
        byte[] mockImageBytes = "mock-image-content".getBytes();
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(s3Buckets.getPhotoAlbum()).thenReturn("photo-album");
        when(s3Service.downloadObject(s3Buckets.getPhotoAlbum(), key))
                .thenReturn(mockImageBytes);

        mockMvc
                .perform(
                        get("/api/v1/photos/1/image"))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(mockImageBytes))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeletePhotoWhenIdValid() throws Exception {
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photos.get(0)));
        doNothing().when(photoRepository).delete(photos.get(0));
        doNothing().when(s3Service).deleteObject("any", "any");
        mockMvc.perform(delete("/api/v1/photos/1"))
                .andExpect(status().isNoContent());

    }

    @Test
    void shouldNotDeletePhotoWhenIdIsInvalid() throws Exception {
        when(photoRepository.findById(999L)).thenThrow(PhotoNotFoundException.class);
        mockMvc.perform(delete("/api/v1/photos/999"))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldDeletePhotosWhenAlbumIsFound() throws Exception {
        when(photoRepository.findByAlbumId(1L)).thenReturn(Optional.of(photos));
        doNothing().when(photoRepository).delete(photos.get(0));
        doNothing().when(s3Service).deleteObject("any", "any");
        mockMvc.perform(delete("/api/v1/photos/album/1"))
                .andExpect(status().isNoContent());

    }

    @Test
    void shouldNotDeletePhotoWhenAlbumIdIsInvalid() throws Exception {
        when(photoRepository.findByAlbumId(999L)).thenThrow(PhotoAlbumNotFoundException.class);
        mockMvc.perform(delete("/api/v1/photos/album/999"))
                .andExpect(status().isNotFound());

    }
}
