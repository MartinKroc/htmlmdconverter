package com.converter.serwer.services;

import com.converter.serwer.dtos.AddFileDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.parboiled.common.FileUtils;
import org.parboiled.common.Preconditions;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class ConverterServiceImpl implements ConverterService {

    private final Path root = Paths.get("uploaddir");

    @Override
    public void fileInit() {
        try {
            Files.createDirectory(root);
        } catch (IOException ex) {
            throw new RuntimeException("Blad, nie zainicjowano folderu plikow");
        }
    }

    @Override
    public void saveFile(MultipartFile multipartFile) {
        try {
            Files.copy(multipartFile.getInputStream(), this.root.resolve(multipartFile.getOriginalFilename()));
        } catch (Exception ex) {
            throw new RuntimeException("Blad przy zapisie pliku: " + ex.getMessage());
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
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public File convertHtmlToMd() {
/*        Document doc = null;
        File input = null;
        try {
            input = new File("uploaddir/test.html");
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return null;
    }

    @Override
    public ResponseEntity<InputStreamResource> convertMdToHtml() throws IOException {
        File mdFile = new File("uploaddir/test.md");
        PegDownProcessor processor = new PegDownProcessor(Extensions.ALL);
        char[] markdown = FileUtils.readAllChars(mdFile);
        Preconditions.checkNotNull(markdown, "The specified file isn't found - "+ mdFile.getName());
        RootNode rootNode = processor.parseMarkdown(markdown);

        List<Node> nodes = rootNode.getChildren();
        StringBuilder content = new StringBuilder();

        for (Node node : nodes) {
            if (node instanceof HeaderNode) {
                HeaderNode headerNode = (HeaderNode) node;
                content.append("<h2>");
                String text = getTextContent(node);
                if (text!=null) {
                    content.append(text);
                }
                content.append("</h2>");
                content.append("\n\n");
            } else if (node instanceof ParaNode) {
                ParaNode paraNode = (ParaNode) node;
                String text = getTextContent(node);
                if (text!=null) {
                    content.append(text);
                }
                content.append("\n\n");
            } else if (node instanceof VerbatimNode) {
                VerbatimNode verbatimNode = (VerbatimNode) node;
                content.append("<code>");
                String text = getTextContent(node);
                if (text!=null) {
                    content.append(text);
                }
                content.append("</code>");
                content.append("\n\n");
            } else if (node instanceof BulletListNode) {
                BulletListNode bulletListNode = (BulletListNode) node;
                content.append("<ul>");
                List<Node> listItemNodes = bulletListNode.getChildren();
                for (Node childNode : listItemNodes) {
                    if (childNode instanceof ListItemNode) {
                        ListItemNode listItemNode = (ListItemNode) childNode;
                        content.append("<li>");
                        String text = getTextContent(childNode);
                        if (text!=null) {
                            content.append(text);
                        }
                        content.append("</li>");
                    }
                }
                content.append("</ul>");
            }
        }
        System.out.print(content);
        //delete file if exist
        File myObj = null;
        File file = new File("src/main/resources/convertedfiles/result.html");
        File fileUploaded = new File("uploaddir/test.md");
        if(file.delete())
        {
            System.out.println("File deleted successfully");
        }
        else
        {
            System.out.println("Failed to delete the file");
        }
        if(fileUploaded.exists()) {
            if(fileUploaded.delete())
            {
                System.out.println("File deleted successfully");
            }
            else
            {
                System.out.println("Failed to delete the file");
            }
        }

        // create file and save
        FileWriter fileWriter = null;
        InputStreamResource resource = null;
        FileInputStream fis = null;
        try {
            myObj = new File("src/main/resources/convertedfiles/result.html");
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
            //fis.close();
            if (fileWriter != null) {
                fileWriter.close();
            }
        }

        return ResponseEntity.ok()
                .contentLength(myObj.length())
                .contentType(MediaType.parseMediaType("text/html"))
                .body(resource);
    }
    private String getTextContent(Node node) {
        if (node instanceof TextNode) {
            return getTextContent((TextNode)node);
        } else if (node instanceof HeaderNode) {
            HeaderNode headerNode = (HeaderNode) node;
            return getTextContent((TextNode) headerNode.getChildren().get(0));
        } else if (node instanceof ParaNode) {
            ParaNode paraNode = (ParaNode) node;
            Node firstChildNode = paraNode.getChildren().get(0);
            if (firstChildNode instanceof SuperNode) {
                return getTextContent((SuperNode) firstChildNode);
            } else if (firstChildNode instanceof TextNode) {
                return getTextContent((TextNode) firstChildNode);
            }
        } else if (node instanceof ListItemNode) {
            ListItemNode listItemNode = (ListItemNode) node;
            RootNode rootNode = (RootNode) listItemNode.getChildren().get(0);
            Node firstChildNode = rootNode.getChildren().get(0);
            if (firstChildNode instanceof SuperNode) {
                return getTextContent((SuperNode) firstChildNode);
            } else if (firstChildNode instanceof TextNode) {
                return getTextContent((TextNode) firstChildNode);
            }
        }
        return null;
    }

    private String getTextContent(SuperNode node) {
        List<Node> nodes = node.getChildren();
        StringBuilder content = new StringBuilder();
        for (Node child : nodes) {
            if (child instanceof TextNode) {
                content.append(getTextContent((TextNode)child));
            } else if (child instanceof SpecialTextNode) {
                content.append(getTextContent((SpecialTextNode)child));
            }
        }
        return content.toString();
    }

    private String getTextContent(TextNode node) {
        return node.getText();
    }
}
