package com.converter.serwer.services;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface HtmlSqlService {
    ResponseEntity<InputStreamResource> convert() throws IOException;
}
