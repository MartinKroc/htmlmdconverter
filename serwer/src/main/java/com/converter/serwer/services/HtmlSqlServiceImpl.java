package com.converter.serwer.services;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HtmlSqlServiceImpl implements HtmlSqlService {

    private final HtmlCsvServiceImpl htmlCsvService;

    @Override
    public ResponseEntity<InputStreamResource> convert() throws IOException {
        Document doc = htmlCsvService.deleteOldFiles();
        //convert

        StringBuilder content = new StringBuilder();
        Elements tables = doc.getElementsByTag("table");
        Elements trs = doc.getElementsByTag("tr");
        Elements tds = doc.getElementsByTag("td");

        int temp = 0;
        String tempRow = "";
        List<String> columns = new ArrayList<String>();
        List<String> rows = new ArrayList<String>();
        int tableNr = 0;

        if(tables.size() > 0) {
            for(Element table : tables) {
                content.append("CREATE TABLE test_table").append(tableNr).append("(\n");
                for(Element tr : trs) {
                    if(temp==0) {
                        for(int i=0; i< tr.childrenSize(); i++) {
                            columns.add(tr.child(i).text());
                            content.append(columns.get(i));
                            content.append(" VARCHAR(100)");
                            if(!(i == tr.childrenSize()-1))  content.append(",\n");
                            else content.append("\n");
                        }
                        content.append(");\n\n");
                        temp++;
                    }
                    else {
                        //INSERTS
                        content.append("INSERT INTO test_table").append(tableNr).append(" VALUES(");
                        for(int i=0; i< tr.childrenSize(); i++) {
                            content.append(tr.child(i).text());
                            if(!(i == tr.childrenSize()-1))  content.append(", ");
                        }
                        content.append(");\n");
                    }
                }
                temp=0;
                tableNr++;
            }
        }

        //create new result file
        FileWriter fileWriter = null;
        InputStreamResource resource = null;
        FileInputStream fis = null;
        File myObj = null;
        try {
            myObj = new File("src/main/resources/convertedfiles/result.md");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            fileWriter = new FileWriter(myObj);
            fileWriter.append(content);
            fis = new FileInputStream(myObj);
            resource = new InputStreamResource(fis);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }

        return ResponseEntity.ok()
                .contentLength(myObj.length())
                .contentType(MediaType.parseMediaType("text/html"))
                .body(resource);
    }
}
