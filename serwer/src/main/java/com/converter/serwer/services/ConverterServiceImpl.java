package com.converter.serwer.services;

import com.converter.serwer.dtos.AddFileDto;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ConverterServiceImpl implements ConverterService {

    private final Path root = Paths.get("uploads");

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

}
