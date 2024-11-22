package dev.lmaruyama.photoalbum.s3;

import dev.lmaruyama.photoalbum.exception.S3ReadingObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.Validate;

import java.io.IOException;

@Service
public class S3Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadObject(String bucketName, String key, byte[] photo) {

        // check if the bucket exists
        boolean doesBucketExists = doesBucketExist(bucketName);
        if (!doesBucketExists) {
            createBucket(bucketName);
        }

        PutObjectRequest putObjectRequest = PutObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(photo));
    }

    private void createBucket(String bucketName) {
        CreateBucketRequest createRequest = CreateBucketRequest.builder()
                            .bucket(bucketName)
                            .build();
        s3Client.createBucket(createRequest);
    }

    private boolean doesBucketExist(String bucketName) {
        try {
            Validate.notEmpty(bucketName, "The bucket name must not be null or an empty string.", "");
            s3Client.getBucketAcl(r -> r.bucket(bucketName));
            return true;
        } catch (AwsServiceException ase) {
            // A redirect error or an AccessDenied exception means the bucket exists but it's not in this region
            // or we don't have permissions to it.
            if ((ase.statusCode() == HttpStatusCode.MOVED_PERMANENTLY) || "AccessDenied".equals(ase.awsErrorDetails().errorCode())) {
                return true;
            }
            if (ase.statusCode() == HttpStatusCode.NOT_FOUND) {
                return false;
            }
            throw ase;
        }
    }

    public void deleteObject(String bucketName, String key) {
        boolean doesBucketExist = doesBucketExist(bucketName);
        if (!doesBucketExist) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        }
    }

    public byte[] downloadObject(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build();
        final ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);

        try {
            return object.readAllBytes();
        } catch (IOException e) {
            final String errorMessage =
                    "Error trying to download the object [%s] from the bucket [%s]. Error message: %s"
                            .formatted(key, bucketName, e.getMessage());
            LOGGER.error(errorMessage, e);
            throw new S3ReadingObjectException("It was not possible to download the object %s from bucket %s".formatted(key, bucketName));
        }
    }
}
