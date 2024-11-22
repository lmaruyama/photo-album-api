package dev.lmaruyama.photoalbum.photo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;

@Entity(name = "photo")
public class Photo {

        @Id
        @SequenceGenerator(
                name = "photo_id_seq",
                sequenceName = "photo_id_seq",
                allocationSize = 1
        )
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "photo_id_seq"
        )
        private Long id;
        private String imageKey;
        private String description;
        @NotNull
        private Long albumId;

        public Photo() {
        }

        public Photo(Long id, String imageKey, String description, Long albumId) {
                this.id = id;
                this.imageKey = imageKey;
                this.description = description;
                this.albumId = albumId;
        }

        public Photo(String imageKey, String description, Long albumId) {
                this.imageKey = imageKey;
                this.description = description;
                this.albumId = albumId;
        }

        public Long getId() {
                return id;
        }

        public Photo setId(Long id) {
                this.id = id;
                return this;
        }

        public String getImageKey() {
                return imageKey;
        }

        public Photo setImageKey(String key) {
                this.imageKey = key;
                return this;
        }

        public String getDescription() {
                return description;
        }

        public Photo setDescription(String description) {
                this.description = description;
                return this;
        }

        public Long getAlbumId() {
                return albumId;
        }

        public Photo setAlbumId(Long albumId) {
                this.albumId = albumId;
                return this;
        }

        @Override
        public String toString() {
                return "Photo{" +
                       "id=" + id +
                       ", key='" + imageKey + '\'' +
                       ", description='" + description + '\'' +
                       ", albumId=" + albumId +
                       '}';
        }
}
