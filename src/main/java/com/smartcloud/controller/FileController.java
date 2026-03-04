package com.smartcloud.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.io.InputStream;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.smartcloud.dto.FileListItemDto;
import com.smartcloud.dto.FileResponseDto;
import com.smartcloud.entity.FileObject;
import com.smartcloud.entity.User;
import com.smartcloud.http.HttpResponse;
import com.smartcloud.service.StorageService;
import com.smartcloud.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.smartcloud.service.FileObjectService;

@RestController
@Tag(name = "Archivos", description = "Gestión de archivos en el sistema")
@RequestMapping("/api/files")
public class FileController {

    private final StorageService storageService;
    private final FileObjectService fileObjectService;
    private final UserService userService;

    public FileController(
            StorageService storageService,
            FileObjectService fileObjectService,
            UserService userService
    ) {
        this.storageService = storageService;
        this.fileObjectService = fileObjectService;
        this.userService = userService;
    }

    @Operation(summary = "Subir un archivo", description = "Permite a los usuarios subir un archivo al sistema. El archivo se asocia con el usuario que lo sube.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) throws Exception {

        User user = userService.getEntityByUsername(principal.getName());
        FileObject saved = storageService.upload(file, user);

        return HttpResponse.created(
                FileResponseDto.fromEntity(saved)
        );
    }

    @Operation(summary = "Listar archivos", description = "Obtiene una lista de archivos asociados al usuario autenticado.")
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            Principal principal
    ) {
        User user = userService.getEntityByUsername(principal.getName());

        List<FileListItemDto> files = fileObjectService.getByUser(user)
                .stream()
                .map(FileListItemDto::fromEntity)
                .toList();

        return HttpResponse.ok(files);
    }
    @Operation(summary = "Descargar un archivo", description = "Permite a los usuarios descargar un archivo específico que les pertenece.")
    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(
            @PathVariable Long id,
            Principal principal
    ) throws Exception {

        User user = userService.getEntityByUsername(principal.getName());
        FileObject file = fileObjectService.getById(id);

        if (!file.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        InputStream is = storageService.download(file);

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=\"" + file.getOriginalFilename() + "\""
                )
                .body(new InputStreamResource(is));
    }
    @Operation(summary = "Eliminar un archivo", description = "Permite a los usuarios eliminar un archivo específico que les pertenece.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable Long id,
            Principal principal
    ) throws Exception {

        User user = userService.getEntityByUsername(principal.getName());
        FileObject file = fileObjectService.getById(id);

        if (!file.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        storageService.delete(file);
        fileObjectService.delete(file);

        return HttpResponse.noContent();
    }
}