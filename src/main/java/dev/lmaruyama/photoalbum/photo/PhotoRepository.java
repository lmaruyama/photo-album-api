package dev.lmaruyama.photoalbum.photo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Optional<List<Photo>> findByAlbumId(Long albumId);
}
