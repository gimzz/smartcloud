package com.smartcloud.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.smartcloud.entity.FileObject;
import com.smartcloud.entity.User;
import com.smartcloud.exception.NotFoundException;
import com.smartcloud.repository.FileObjectRepository;

@Service
@Transactional
public class FileObjectService {

    private final FileObjectRepository repository;

    public FileObjectService(FileObjectRepository repository) {
        this.repository = repository;
    }

    public FileObject save(FileObject fileObject) {
        return repository.save(fileObject);
    }

    public Page<FileObject> getByUser(User user, int page, int size) {
        return repository.findByOwner(user, PageRequest.of(page, size));
    }

    public FileObject getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("File not found"));
    }

    public void delete(FileObject file) {
        repository.delete(file);
    }
}