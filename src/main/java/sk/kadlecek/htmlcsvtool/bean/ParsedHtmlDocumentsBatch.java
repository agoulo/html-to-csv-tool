package sk.kadlecek.htmlcsvtool.bean;

import java.util.ArrayList;
import java.util.List;

public class ParsedHtmlDocumentsBatch {

    private List<ParsedHtmlDocument> documentsInBatch = new ArrayList<>();

    public void addParsedHtmlDocumentToBatch(ParsedHtmlDocument parsedHtmlDocument) {
        documentsInBatch.add(parsedHtmlDocument);
    }

    public void addParsedHtmlDocumentsBatch(ParsedHtmlDocumentsBatch batch) {
        documentsInBatch.addAll(batch.getDocumentsInBatch());
    }

    public List<ParsedHtmlDocument> getDocumentsInBatch() {
        return documentsInBatch;
    }

    public List<ParsedResource> getAllParsedResources() {
        List<ParsedResource> parsedResources = new ArrayList<>();
        for (ParsedHtmlDocument parsedDocument: documentsInBatch) {
            if (parsedDocument.getParsedResource() != null) {
                parsedResources.add(parsedDocument.getParsedResource());
            }
        }
        return parsedResources;
    }


    public ParsedResourcesTable getParsedResourcesTable() {
        return new ParsedResourcesTable(getAllParsedResources());
    }
}
