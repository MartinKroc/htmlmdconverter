package com.converter.serwer.conf;

import com.converter.serwer.services.FilesService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FileDirInit implements ApplicationRunner {

    @Resource
    FilesService filesService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        filesService.deleteFile();
        filesService.fileInit();
    }
}
