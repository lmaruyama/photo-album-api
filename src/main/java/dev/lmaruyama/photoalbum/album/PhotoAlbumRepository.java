package dev.lmaruyama.photoalbum.album;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {
}
