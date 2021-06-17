package com.converter.serwer.services;

import com.converter.serwer.controllers.ConverterController;
import com.converter.serwer.dtos.FileInfo;
import com.converter.serwer.dtos.resp.FilesHistoryDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilesServiceImpl implements FilesService {

    private final Path root = Paths.get("uploaddir");
    private final Path historyRoot = Paths.get("history");
    private int tempFileNum = 1;

    @Override
    public void fileInit() {
        try {
            Files.createDirectory(root);
        } catch (IOException ex) {
            throw new RuntimeException("Blad, nie zainicjowano folderu plikow");
        }
    }

    public void historyFilesInit() {
        try {
            Files.createDirectory(historyRoot);
        } catch (IOException ex) {
            throw new RuntimeException("Blad, nie zainicjowano folderu plikow");
        }
    }

    @Override
    public void saveFile(MultipartFile multipartFile) {
        if(multipartFile.getContentType().equals("text/html")) {
            try {
                Files.copy(multipartFile.getInputStream(), this.root.resolve("test.html"), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                throw new RuntimeException("Blad przy zapisie pliku: " + ex.getMessage());
            }
        }
        else {
            try {
                Files.copy(multipartFile.getInputStream(), this.root.resolve("test.md"), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                throw new RuntimeException("Blad przy zapisie pliku: " + ex.getMessage());
            }
        }
    }

    @Override
    public Resource loadFile(String fileName) {
        try {
            Path file = root.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new RuntimeException("Blad przy otwieraniu pliku");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void deleteFile() {
        FileSystemUtils.deleteRecursively(root.toFile());
        FileSystemUtils.deleteRecursively(historyRoot.toFile());
    }

    @Override
    public String deleteUpFiles() {
        List<FilesHistoryDto> fh = new ArrayList<FilesHistoryDto>();
        String date;
        File dir = new File(root.toUri());
        File[] list = dir.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile()) {
                list[i].delete();
            } else if (list[i].isDirectory()) {
                System.out.println("Directory " + list[i].getName());
            }
        }
        return "Ok";
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(ConverterController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @Override
    public ResponseEntity<Resource> getFile(String filename) {
        Resource file = loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    public void pushFileToHistory(File file, String fileType) throws IOException {
        String path = "history/";
        InputStream is = null;
        OutputStream os = null;
        File toSave;
        switch (fileType) {
            case "html":
                toSave = new File(path + "converted" + tempFileNum + ".html");
                break;
            case "md" :
                toSave = new File(path + "converted" + tempFileNum + ".md");
                break;
            case "sql" :
                toSave = new File(path + "converted" + tempFileNum + ".sql");
                break;
            case "csv" :
                toSave = new File(path + "converted" + tempFileNum + ".csv");
                break;
            default: {
                toSave = new File(path + "converted" + tempFileNum + ".txt");
            }
        }
        try {
            is = new FileInputStream(file);
            os = new FileOutputStream(toSave);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
        tempFileNum++;
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFromHistory(String filename) {
        InputStreamResource resource = null;
        FileInputStream fis = null;
        File myObj = null;
        try {
            myObj = new File("history/" + filename);
            fis = new FileInputStream(myObj);
            resource = new InputStreamResource(fis);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .contentLength(myObj.length())
                .contentType(MediaType.parseMediaType("text/html"))
                .body(resource);
    }

    public ResponseEntity<List<FilesHistoryDto>> getFilesFromHistory() {
        List<FilesHistoryDto> fh = new ArrayList<FilesHistoryDto>();
        String date;
        File dir = new File(historyRoot.toUri());
        File[] list = dir.listFiles();
        System.out.println("path " + dir.getName());
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile()) {
                System.out.println("File " + list[i].getName());
                date = convertTime(list[i].lastModified());
                fh.add(FilesHistoryDto.builder().size(String.valueOf(list[i].length())).name(list[i].getName()).dateOfConversion(date).build());
            } else if (list[i].isDirectory()) {
                System.out.println("Directory " + list[i].getName());
            }
        }
        return ResponseEntity.ok(fh);
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }
}
