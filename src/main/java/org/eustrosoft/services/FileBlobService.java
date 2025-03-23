package org.eustrosoft.services;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.File;
import org.eustrosoft.entitites.FileBlob;
import org.eustrosoft.repositories.FileBlobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;

@Service
@Transactional
@RequiredArgsConstructor
public class FileBlobService {
    private final FileBlobRepository repository;

    @Transactional
    public FileBlob save(FileBlob blob) {
        if (blob == null) {
            throw new IllegalArgumentException("Blob could not be null while saving");
        }
        return repository.save(blob);
    }

    @Transactional
    public FileBlobRepository.BlobResponse getFileChunk(Long fileId, Long no) {
        if (fileId == null) {
            throw new IllegalArgumentException("File could not be null");
        }
        return repository.getByZOIDAndNo(fileId, no);
    }
}
