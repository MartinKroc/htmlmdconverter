package com.converter.serwer.services;

import com.converter.serwer.controllers.ConverterController;
import com.converter.serwer.dtos.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilesServiceImpl implements FilesService {

    private final Path root = Paths.get("uploaddir");

    @Override
    public void fileInit() {
        try {
            Files.createDirectory(root);
        } catch (IOException ex) {
            throw new RuntimeException("Blad, nie zainicjowano folderu plikow");
        }
    }

    @Override
    public void saveFile(MultipartFile multipartFile) {
        try {
            Files.copy(multipartFile.getInputStream(), this.root.resolve(multipartFile.getOriginalFilename()));
        } catch (Exception ex) {
            throw new RuntimeException("Blad przy zapisie pliku: " + ex.getMessage());
        }
    }

    @Override
    public Resource loadFile(String fileName) {
        try {
            Path file = root.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new RuntimeException("Blad przy otwieraniu pliku");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void deleteFile() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(ConverterController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @Override
    public ResponseEntity<Resource> getFile(String filename) {
        Resource file = loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
