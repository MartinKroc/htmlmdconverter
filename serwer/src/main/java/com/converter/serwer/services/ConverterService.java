package com.converter.serwer.services;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ConverterService {

    ResponseEntity<InputStreamResource> convertHtmlToMd() throws IOException;
    ResponseEntity<InputStreamResource> convertMdToHtml() throws IOException;

}
