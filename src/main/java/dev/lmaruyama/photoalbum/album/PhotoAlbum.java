package dev.lmaruyama.photoalbum.album;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;

@Entity(name = "album")
public class PhotoAlbum{

        @Id
        @SequenceGenerator(
                name = "album_id_seq",
                sequenceName = "album_id_seq",
                allocationSize = 1
        )
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "album_id_seq"
        )
        private Long id;
        @NotEmpty(message = "The album's title must not be empty")
        private String title;
        private String location;
        private String description;
        private LocalDate eventDate;

        public PhotoAlbum(Long id, String title, String location, String description, LocalDate eventDate) {
                this.id = id;
                this.title = title;
                this.location = location;
                this.description = description;
                this.eventDate = eventDate;
        }

        public PhotoAlbum(String title, String location, String description, LocalDate eventDate) {
                this.title = title;
                this.location = location;
                this.description = description;
                this.eventDate = eventDate;
        }

        public PhotoAlbum() {
        }

        public Long getId() {
                return id;
        }

        public PhotoAlbum setId(Long id) {
                this.id = id;
                return this;
        }

        public String getTitle() {
                return title;
        }

        public PhotoAlbum setTitle(String title) {
                this.title = title;
                return this;
        }

        public String getLocation() {
                return location;
        }

        public PhotoAlbum setLocation(String location) {
                this.location = location;
                return this;
        }

        public String getDescription() {
                return description;
        }

        public PhotoAlbum setDescription(String description) {
                this.description = description;
                return this;
        }

        public LocalDate getEventDate() {
                return eventDate;
        }

        public PhotoAlbum setEventDate(LocalDate eventDate) {
                this.eventDate = eventDate;
                return this;
        }

        @Override
        public String toString() {
                return "PhotoAlbum{" +
                       "id=" + id +
                       ", title='" + title + '\'' +
                       ", location='" + location + '\'' +
                       ", description='" + description + '\'' +
                       ", eventDate=" + eventDate +
                       '}';
        }
}
