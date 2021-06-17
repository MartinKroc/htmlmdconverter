package com.converter.serwer.dtos.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class FilesHistoryDto {
    private String name;
    private String size;
    private String dateOfConversion;
}
