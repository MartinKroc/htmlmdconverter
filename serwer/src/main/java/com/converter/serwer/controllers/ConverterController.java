package com.converter.serwer.controllers;

import com.converter.serwer.dtos.AddFileDto;
import com.converter.serwer.dtos.FileInfo;
import com.converter.serwer.services.ConverterService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/convert")
@AllArgsConstructor
public class ConverterController {

    private final ConverterService converterService;

    @GetMapping
    public String test() {
        return "test okej";
    }

    @PostMapping("/upload")
    public String addFile(@RequestParam("file")MultipartFile file) {
        String m = "";
        try {
            converterService.saveFile(file);
            m = "Plik dodany pomyslnie: " + file.getOriginalFilename();
            return "Ok";
        } catch (Exception ex) {
            return "Not ok";
        }
    }

/*    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getFiles() {

    }*/
}
