package dev.lmaruyama.photoalbum.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3.buckets")
public class S3Buckets {

    private String photoAlbum;

    public String getPhotoAlbum() {
        return photoAlbum;
    }

    public S3Buckets setPhotoAlbum(String photoAlbum) {
        this.photoAlbum = photoAlbum;
        return this;
    }
}
