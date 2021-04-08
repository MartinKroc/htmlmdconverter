package com.converter.serwer.controllers;

import com.converter.serwer.dtos.AddFileDto;
import com.converter.serwer.dtos.FileInfo;
import com.converter.serwer.services.ConverterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import org.springframework.core.io.Resource;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = converterService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(ConverterController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = converterService.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
