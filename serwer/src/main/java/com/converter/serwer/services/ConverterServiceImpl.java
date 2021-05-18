package com.converter.serwer.services;

import com.converter.serwer.dtos.AddFileDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
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
    public ResponseEntity<InputStreamResource> convertHtmlToMd() throws IOException {

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
        StringBuilder content = new StringBuilder();
        String tempTag = "";
        //System.out.print(doc);

        Elements elements = doc.body().select("*");
        for(Element element : elements) {
            System.out.print(element.tagName());
            tempTag = element.tagName();
            switch (tempTag) {
                case "h1" :
                    content.append("# ").append(element.ownText()).append("\n");
                    break;
                case "h3" :
                    content.append("### ").append(element.ownText()).append("\n");
                    break;
                case "p" :
                    content.append(element.ownText()).append("\n");
                    break;
                case "br" :
                    content.append("\n");
                    break;
                case "strong" :
                    content.append("**").append(element.ownText()).append("**").append("\n");
                    break;
                case "em" :
                    content.append("*").append(element.ownText()).append("*").append("\n");
                    break;
                case "blockquote" :
                    content.append("> ").append(element.ownText()).append("\n");
                    break;
                case "ol" :
                    for(int i=1; i<=element.childrenSize(); i++) {
                        content.append(i).append(" ").append(element.child(i-1).ownText()).append("\n");
                    }
                    break;
                case "ul" :
                    for(int i=1; i<=element.childrenSize(); i++) {
                        content.append("- ").append(element.child(i-1).ownText()).append("\n");
                    }
                    break;
                case "img" :
                    Attributes a = element.attributes();
                    String link = a.get("src");
                    content.append("![image](").append(link).append(")\n");
                    break;
                case "a" :
                    Attributes aa = element.attributes();
                    String linka = aa.get("href");
                    content.append("[").append(element.ownText()).append("](").append(linka).append(")\n");
                    break;
                case "code" :
                    content.append("    ").append(element.ownText()).append("\n");
                    break;
                case "li" :
                    break;
                default:
                    content.append(element.ownText()).append("\n");
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

    @Override
    public ResponseEntity<InputStreamResource> convertMdToHtml() throws IOException {
        File mdFile = new File("uploaddir/test.md");
        PegDownProcessor processor = new PegDownProcessor(Extensions.ALL);
        char[] markdown = FileUtils.readAllChars(mdFile);
        Preconditions.checkNotNull(markdown, "The specified file isn't found - "+ mdFile.getName());
        RootNode rootNode = processor.parseMarkdown(markdown);

        List<Node> nodes = rootNode.getChildren();
        StringBuilder content = new StringBuilder();

        String temp1;
        String temp2;
        for (Node node : nodes) {
            System.out.print(node);
            if (node instanceof HeaderNode) {
                HeaderNode headerNode = (HeaderNode) node;
                temp1 = "<h" + headerNode.getLevel() + ">";
                temp2 = "</h" + headerNode.getLevel() + ">";
                content.append(temp1);
                String text = getTextContent(node);
                if (text!=null) {
                    content.append(text);
                }
                content.append(temp2);
                content.append("\n\n");
            }
            else if (node instanceof ParaNode) {
                ParaNode paraNode = (ParaNode) node;
                String text = getTextContent(node);
                if (text!=null) {
                    content.append(text);
                }
                content.append("\n\n");
            }
            else if (node instanceof VerbatimNode) {
                VerbatimNode verbatimNode = (VerbatimNode) node;
                content.append("<code>");
                String text = getTextContent(node);
                if (text!=null) {
                    content.append(text);
                }
                content.append("</code>");
                content.append("\n\n");
            }
            else if (node instanceof BlockQuoteNode) {
                BlockQuoteNode blockQuoteNode = (BlockQuoteNode) node;
                content.append("<blockquote>");
                String text = getTextContent(node);
                if (text!=null) {
                    content.append(text);
                }
                content.append("</blockquote>");
                content.append("\n\n");
            }
            else if (node instanceof StrikeNode) {
                StrongEmphSuperNode strongEmphSuperNode = (StrongEmphSuperNode) node;
                StrikeNode strikeNode = (StrikeNode) node;
                if(strongEmphSuperNode.isStrong()) {
                    content.append("<strong>");
                    String text = getTextContent(node);
                    if (text!=null) {
                        content.append(text);
                    }
                    content.append("</strong>");
                }
                else {
                    content.append("<em>");
                    String text = getTextContent(node);
                    if (text!=null) {
                        content.append(text);
                    }
                    content.append("</em>");
                }
                content.append("\n\n");
            }
            else if (node instanceof BulletListNode) {
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
            else if (node instanceof OrderedListNode) {
                OrderedListNode orderedListNode = (OrderedListNode) node;
                content.append("<ol>");
                List<Node> listItemOlNodes = orderedListNode.getChildren();
                for (Node childNode : listItemOlNodes) {
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
                content.append("</ol>");
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

    @Override
    public ResponseEntity<InputStreamResource> convertMDToXml() {
        return null;
    }

    private String getTextContent(Node node) {
        if (node instanceof TextNode) {
            return getTextContent((TextNode)node);
        }
        else if (node instanceof HeaderNode) {
            HeaderNode headerNode = (HeaderNode) node;
            return getTextContent((TextNode) headerNode.getChildren().get(0));
        }
        else if (node instanceof BlockQuoteNode) {
            BlockQuoteNode blockQuoteNode = (BlockQuoteNode) node;
            return getTextContent((SuperNode) blockQuoteNode.getChildren().get(0));
        }
        else if (node instanceof StrongEmphSuperNode) {
            StrongEmphSuperNode strongEmphSuperNode = (StrongEmphSuperNode) node;
            Node firstChildNode = strongEmphSuperNode.getChildren().get(0);
            if (firstChildNode instanceof SuperNode) {
                return getTextContent((SuperNode) firstChildNode);
            }
            else if (firstChildNode instanceof TextNode) {
                return getTextContent((TextNode) firstChildNode);
            }
        }
        else if (node instanceof ParaNode) {
            ParaNode paraNode = (ParaNode) node;
            Node firstChildNode = paraNode.getChildren().get(0);
            System.out.println("first child node " + firstChildNode);
            if (firstChildNode instanceof SuperNode) {
                Node sChild = firstChildNode.getChildren().get(0);
                System.out.println("sChild node " + sChild);
                String li = "link";
                if(sChild instanceof ExpLinkNode) {
                    ExpLinkNode eln = (ExpLinkNode) firstChildNode.getChildren().get(0);
                    System.out.println(eln.url);
                    String appendL = li + "<a " + "href=\"" + eln.url + "\">" + li + "</a>" + eln.title;
                    return appendL;
                }
                else if(sChild instanceof ExpImageNode) {
                    ExpImageNode ein = (ExpImageNode)firstChildNode.getChildren().get(0);
                    System.out.println("zdjecie" + ein.getChildren().get(0));
                    String appendImg = "<img " + "src=\"" + ein.url + "\">" + "</img>";
                    return appendImg;
                }
                else {
                    return getTextContent((SuperNode) firstChildNode);
                }
            }
            else if (firstChildNode instanceof TextNode) {
                return getTextContent((TextNode) firstChildNode);
            }
        }
        else if (node instanceof ListItemNode) {
            ListItemNode listItemNode = (ListItemNode) node;
            RootNode rootNode = (RootNode) listItemNode.getChildren().get(0);
            Node firstChildNode = rootNode.getChildren().get(0);
            if (firstChildNode instanceof SuperNode) {
                return getTextContent((SuperNode) firstChildNode);
            }
            else if (firstChildNode instanceof TextNode) {
                return getTextContent((TextNode) firstChildNode);
            }
        }
        return null;
    }

    private String getTextContent(SuperNode node) {
        List<Node> nodes = node.getChildren();
        StringBuilder content = new StringBuilder();
        for (Node child : nodes) {
            if (child instanceof VerbatimNode) {
                content.append(getTextContent((VerbatimNode)child));
            }
            else if (child instanceof SpecialTextNode) {
                content.append(getTextContent((SpecialTextNode)child));
            }
            else if (child instanceof InlineHtmlNode) {
                content.append(getTextContent((InlineHtmlNode)child));
            }
            else if (child instanceof HtmlBlockNode) {
                content.append(getTextContent((HtmlBlockNode) child));
            }
            else if (child instanceof CodeNode) {
                content.append(getTextContent((CodeNode)child));
            }
            else if (child instanceof TextNode) {
                content.append(getTextContent((TextNode)child));
            }
            else if (child instanceof SuperNode) {
                if(child instanceof StrongEmphSuperNode) {
                    StrongEmphSuperNode sn = (StrongEmphSuperNode)child;
                    if(sn.isStrong()) {
                        content.append("<strong>");
                        content.append(getTextContent((TextNode)child.getChildren().get(0)));
                        content.append("</strong>");
                    }
                    else {
                        content.append("<em>");
                        content.append(getTextContent((TextNode)child.getChildren().get(0)));
                        content.append("</em>");
                    }
                }
                else {
                    content.append(getTextContent((TextNode)child.getChildren().get(0)));
                }
            }
        }
        return content.toString();
    }

    private String getTextContent(TextNode node) {
        return node.getText();
    }
    private String getTextContent(SpecialTextNode node) {
        return node.getText();
    }
    private String getTextContent(InlineHtmlNode node) {
        return node.getText();
    }
    private String getTextContent(HtmlBlockNode node) {
        return node.getText();
    }
    private String getTextContent(CodeNode node) {
        return node.getText();
    }
}
