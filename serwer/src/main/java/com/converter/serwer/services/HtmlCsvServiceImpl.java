package com.converter.serwer.services;

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
public class HtmlCsvServiceImpl implements HtmlCsvService {

    @Override
    public ResponseEntity<InputStreamResource> convert() throws IOException {
        Document doc = null;
        //delete old files
        try {
            File file = new File("uploaddir/test.html");
            doc = Jsoup.parse(file, "UTF-8");
            File fileUp = new File("src/main/resources/convertedfiles/result.md");

            if(fileUp.delete())
            {
                System.out.println("File deleted successfully");
            }
            else
            {
                System.out.println("Failed to delete the file");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //parse
        //System.out.print(doc);
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
                for(Element tr : trs) {
                    if(temp==0) {
                        for(int i=0; i< tr.childrenSize(); i++) {
                            columns.add(tr.child(i).text());
                            content.append(columns.get(i));
                            if(!(i == tr.childrenSize()-1))  content.append(",");
                        }
                        content.append("\n");
                        temp++;
                    }
                    else {
                        for(int i=0; i< tr.childrenSize(); i++) {
                            content.append(tr.child(i).text());
                            if(!(i == tr.childrenSize()-1))  content.append(",");
                        }
                        content.append("\n");
                    }
                }
                temp=0;
                tableNr++;
            }
        }
        System.out.print(content);

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
