package dev.lmaruyama.photoalbum.album;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lmaruyama.photoalbum.photo.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({PhotoAlbumController.class, PhotoAlbumService.class})
@AutoConfigureMockMvc
class PhotoAlbumControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PhotoAlbumRepository repository;

    @MockBean
    private PhotoService photoService;

    List<PhotoAlbum> albums = new ArrayList<>();

    @BeforeEach
    void setup() {
        albums = List.of(
            new PhotoAlbum(1L, "My First Time in Europe", "Spain, France, UK", "", LocalDate.of(2018, Month.NOVEMBER,1)),
            new PhotoAlbum(2L, "Road trip to Scotland", "Scotland", "", LocalDate.of(2021, Month.JUNE,30))
        );
    }

    @Test
    void shouldReturnAllPhotoAlbums() throws Exception {
        String jsonContent = """
          [
             {
               "id": 1,
               "title": "My First Time in Europe",
               "location": "Spain, France, UK",
               "description": "",
               "eventDate": "2018-11-01"
             },
             {
               "id": 2,
               "title": "Road trip to Scotland",
               "location": "Scotland",
               "description": "",
               "eventDate": "2021-06-30"
             }
           ]
        """;
        when(repository.findAll()).thenReturn(albums);
        mockMvc.perform(get("/api/v1/photo-albums"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }


    @Test
    void shouldReturnPhotoAlbumById_whenTheIdIsValid() throws Exception {
        String jsonContent = """
             {
               "id": 1,
               "title": "My First Time in Europe",
               "location": "Spain, France, UK",
               "description": "",
               "eventDate": "2018-11-01"
             }
        """;

        final PhotoAlbum photoAlbum = albums.get(0);
        when(repository.findById(1L)).thenReturn(Optional.of(photoAlbum));
        mockMvc.perform(get("/api/v1/photo-albums/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void shouldReturnHttpStatusNotFound_whenTheIdIsInvalid() throws Exception {
         mockMvc.perform(get("/api/v1/photo-albums/999"))
                 .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreatePhotoAlbum() throws Exception {
        final PhotoAlbum photoAlbumRequest =
                new PhotoAlbum("Day trip to London", "London", "", LocalDate.of(2024, Month.JANUARY, 25));

        final PhotoAlbum photoAlbumResponse =
                new PhotoAlbum(3L, "Day trip to London", "London", "", LocalDate.of(2024, Month.JANUARY, 25));;

        when(repository.save(any(PhotoAlbum.class))).thenReturn(photoAlbumResponse);

        mockMvc.perform(post("/api/v1/photo-albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoAlbumRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(photoAlbumResponse)));
    }

    @Test
    void shouldNotCreatePhotoAlbum_whenTheTitleIsInvalid() throws Exception {
        final PhotoAlbum photoAlbumRequest =
                new PhotoAlbum(null, "", "London", "", LocalDate.of(2024, Month.JANUARY, 25));

        mockMvc.perform(post("/api/v1/photo-albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(photoAlbumRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePhotoAlbum() throws Exception {
        final PhotoAlbum updated =
                new PhotoAlbum(1L, "Day trip to London and Brighton", "London and Brighton", "", LocalDate.of(2024, Month.JANUARY, 24));
        when(repository.findById(1L)).thenReturn(Optional.of(albums.get(0)));
        when(repository.save(updated)).thenReturn(updated);

        mockMvc.perform(put("/api/v1/photo-albums/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeletePhotoAlbumIfIdIsValid() throws Exception {
        doNothing().when(repository).deleteById(1L);
        mockMvc.perform(delete("/api/v1/photo-albums/1"))
                .andExpect(status().isNoContent());

        verify(photoService, times(1)).deletePhotosFromAlbum(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
