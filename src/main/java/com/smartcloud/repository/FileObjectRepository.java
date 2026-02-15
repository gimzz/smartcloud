package com.smartcloud.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.smartcloud.entity.FileObject;
import com.smartcloud.entity.User;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
    List<FileObject> findByOwner(User owner);
    Optional<FileObject> findByObjectKey(String objectKey);
    
}
