package com.smartcloud.repository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.smartcloud.entity.FileObject;
import com.smartcloud.entity.User;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
    Page<FileObject> findByOwner(User owner, Pageable pageable);
    Optional<FileObject> findByObjectKeyOriginal(String objectKeyOriginal);
}