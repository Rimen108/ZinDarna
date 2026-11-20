package com.zindarna.projectservice.service;


import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PhotoService {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final GridFSBucket gridFSBucket;

    public PhotoService(GridFSBucket gridFSBucket) {
        this.gridFSBucket = gridFSBucket;
    }


    private void validatePhoto(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("La photo est vide.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "La taille de la photo ne doit pas dépasser 10 MB."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Le fichier doit être une image."
            );
        }
    }



    public String uploadPhoto(MultipartFile file) throws IOException {
        validatePhoto(file);
        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(1024 * 1024)
                .metadata(
                        new org.bson.Document("contentType", file.getContentType())
                );

        ObjectId fileId = gridFSBucket.uploadFromStream(
                file.getOriginalFilename(),
                file.getInputStream(),
                options
        );

        return fileId.toHexString();
    }


    public byte[] getPhoto(String photoId) throws IOException {

        ObjectId objectId = validatePhotoId(photoId);

        try (var outputStream = new java.io.ByteArrayOutputStream()) {

            gridFSBucket.downloadToStream(objectId, outputStream);

            return outputStream.toByteArray();
        }



    }

    public void deletePhoto(String photoId) {

        ObjectId objectId = validatePhotoId(photoId);

        gridFSBucket.delete(objectId);
    }


    private ObjectId validatePhotoId(String photoId) {

        if (photoId == null || !ObjectId.isValid(photoId)) {
            throw new IllegalArgumentException("Identifiant de photo invalide.");
        }

        return new ObjectId(photoId);
    }



}
