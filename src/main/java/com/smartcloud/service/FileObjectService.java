package com.smartcloud.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<FileObject> getByUser(User user) {
        return repository.findByOwner(user);
    }

    public FileObject getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("File not found"));
    }

    public void delete(FileObject file) {
        repository.delete(file);
    }
}