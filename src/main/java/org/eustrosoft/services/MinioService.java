package org.eustrosoft.services;

import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.configurations.MinioConfiguration;
import org.eustrosoft.utils.CommonUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;
    private final MinioConfiguration minioConfiguration;

    @SneakyThrows
    public String getBucketName() {
        return minioConfiguration.getBucket();
    }

    @SneakyThrows
    public List<Bucket> list() {
        return minioClient.listBuckets();
    }

    @SneakyThrows
    public StatObjectResponse getObjectMetadata(String key) {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(minioConfiguration.getBucket())
                        .object(key)
                        .build()
        );
    }

    @SneakyThrows
    public GetObjectResponse getObject(String key) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfiguration.getBucket())
                        .object(key)
                        .build()
        );
    }

    @SneakyThrows
    public List<Result<Item>> listObjects(String key) {
        String finalKey = key;
        if (key.startsWith("/")) {
            finalKey = key.substring(1);
        }
        if (!key.endsWith("/")) {
            finalKey = finalKey + "/";
        }
        return CommonUtils.iterableToList(
                minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(minioConfiguration.getBucket())
                                .prefix(finalKey)
                                .build()
                ));
    }

    @SneakyThrows
    public byte[] getObjectBytes(String key, Long offset, Long length) {
        if (offset == null || length == null || length <= 0) {
            return new byte[0];
        }
        byte[] bytes;
        try (GetObjectResponse gor = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfiguration.getBucket())
                        .object(key).offset(offset).length(length).build())
        ) {
            bytes = IOUtils.toByteArray(gor);
        }
        return bytes;
    }

    @SneakyThrows
    public byte[] getObjectBytes(String key) {
        if (StringUtils.isEmpty(key)) {
            return new byte[0];
        }
        byte[] bytes;
        try (GetObjectResponse gor = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfiguration.getBucket())
                        .object(key).build())
        ) {
            bytes = IOUtils.toByteArray(gor);
        }
        return bytes;
    }

    public void putObject(final String key, final InputStream inputStream, final String contentType) {
        try (InputStream stream = inputStream) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfiguration.getBucket())
                    .object(key)
                    .contentType(contentType)
                    .stream(stream, stream.available(), -1)
                    .build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SneakyThrows
    public void deleteObject(final String key) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioConfiguration.getBucket())
                        .object(key)
                        .build()
        );
    }

    @SneakyThrows
    public void deleteAllFilesInDirectory(final String key) {
        List<Result<Item>> results = listObjects(key);
        if (results == null) {
            return;
        }
        results.forEach(res -> {
            try {
                Item item = res.get();
                deleteObject(item.objectName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
