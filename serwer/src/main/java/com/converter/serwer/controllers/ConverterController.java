package com.converter.serwer.controllers;

import com.converter.serwer.dtos.FileInfo;
import com.converter.serwer.dtos.resp.FilesHistoryDto;
import com.converter.serwer.services.ConverterService;
import com.converter.serwer.services.FilesService;
import com.converter.serwer.services.HtmlCsvService;
import com.converter.serwer.services.HtmlSqlService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/convert")
@AllArgsConstructor
public class ConverterController {

    private final ConverterService converterService;
    private final HtmlSqlService htmlSqlService;
    private final HtmlCsvService htmlCsvService;
    private final FilesService filesService;

    // FILE MANAGEMENT ENDPOINTS

    @PostMapping("/files/upload")
    public String addFile(@RequestParam("file")MultipartFile file) {
        try {
            filesService.saveFile(file);
            return "upload successful";
        } catch (Exception ex) {
            return "error";
        }
    }

    @GetMapping("/files/list")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        return filesService.getListFiles();
    }

    @GetMapping("/files/list/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        return filesService.getFile(filename);
    }

    @GetMapping("/history/list")
    public ResponseEntity<List<FilesHistoryDto>> getHistory() {
        return filesService.getFilesFromHistory();
    }

    // CONVERSION ENDPOINTS

    @GetMapping(value = "/conv/md")
    public ResponseEntity<InputStreamResource> convertMdToHtml() throws IOException {
        return converterService.convertMdToHtml();
    }

    @GetMapping(value = "/conv/html")
    public ResponseEntity<InputStreamResource> convertHtmlToMD() throws IOException {
        return converterService.convertHtmlToMd();
    }

    @GetMapping(value = "/conv/sql")
    public ResponseEntity<InputStreamResource> convertHtmlToSql() throws IOException {
        return htmlSqlService.convert();
    }

    @GetMapping(value = "/conv/csv")
    public ResponseEntity<InputStreamResource> convertHtmlToCsv() throws IOException {
        return htmlCsvService.convert();
    }
}
