package sk.kadlecek.htmlcsvtool.bean;

import java.io.File;

public class ParsedHtmlDocument {

    /**
     * The path to the file which was parsed into given ParsedHtmlDocument instance.
     */
    private File file;

    /**
     * HTML document title.
     */
    private String title;

    /**
     * HTML document description.
     */
    private String description;

    /**
     * Resource parsed from given HTML document.
     */
    private ParsedResource parsedResource;

    public ParsedHtmlDocument(File file) {
        super();
        this.file = file;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ParsedResource getParsedResource() {
        return parsedResource;
    }

    public void setParsedResource(ParsedResource parsedResource) {
        this.parsedResource = parsedResource;
    }
}
