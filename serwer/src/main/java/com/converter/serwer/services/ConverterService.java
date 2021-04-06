package com.converter.serwer.services;

import com.converter.serwer.dtos.AddFileDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ConverterService {
    public void fileInit();
    public void saveFile(MultipartFile multipartFile);
    public Resource loadFile(String fileName);
    public void deleteFile();
}
