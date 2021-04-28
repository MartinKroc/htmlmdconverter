package com.converter.serwer.services;

import com.converter.serwer.dtos.AddFileDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface ConverterService {
    public void fileInit();
    public void saveFile(MultipartFile multipartFile);
    public Resource loadFile(String fileName);
    public void deleteFile();
    public Stream<Path> loadAll();
    public File convertHtmlToMd();

    ResponseEntity<InputStreamResource> convertMdToHtml() throws IOException;
}
