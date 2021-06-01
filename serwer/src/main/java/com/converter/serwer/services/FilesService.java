package com.converter.serwer.services;

import com.converter.serwer.dtos.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface FilesService {
    void fileInit();
    void saveFile(MultipartFile multipartFile);
    Resource loadFile(String fileName);
    void deleteFile();
    Stream<Path> loadAll();
    ResponseEntity<List<FileInfo>> getListFiles();
    ResponseEntity<Resource> getFile(String filename);
}
