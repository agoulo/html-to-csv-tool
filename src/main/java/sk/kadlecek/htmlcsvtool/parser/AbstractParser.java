package sk.kadlecek.htmlcsvtool.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedHtmlDocument;
import sk.kadlecek.htmlcsvtool.bean.ParsedProduct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParser implements GenericParser {

    protected abstract Logger getLogger();

    protected String CHARSET = "utf-8";

    protected static final String DEFAULT_TITLE_SELECTOR = "html head title";
    protected static final String DEFAULT_DESCRIPTION_SELECTOR = "html head meta[name=description]";

    @Override
    public ParsedHtmlDocument parse(File file, JobConfiguration jobConfiguration) throws IOException {

        getLogger().trace("Parsing file '{}'.", file.getAbsolutePath());

        try {
            Document doc = Jsoup.parse(file, CHARSET);

            ParsedHtmlDocument parsedFile = new ParsedHtmlDocument(file);
            parsedFile.setTitle(parseTitle(doc));
            parsedFile.setDescription(parseDescription(doc));

            // parse resource from document
            parsedFile.setParsedResource(parseResource(file, doc, jobConfiguration));

            return parsedFile;
        }catch(IOException e) {
            getLogger().error("Failed to read file '{}': {}", file.getAbsoluteFile(), e.getMessage());
            throw e;
        }

    }

    protected String generateResourceURI(File file, JobConfiguration jobConfiguration) {
        String filePath = file.getAbsolutePath();
        String filesPath = jobConfiguration.getFilesPath();
        String uriPrefix = jobConfiguration.getUriPrefix();

        // strip the filesPath from filePath to get relative URI, prepend the URI prefix to the relative URI
        String uri = filePath.substring(filesPath.length());
        uri = uriPrefix + uri;
        return uri;
    }

    protected String getStringValueOfFirstElementWithSelector(Document doc, String selector) {
        Elements elements = doc.select(selector);
        if (elements.size() > 0) {
            Element element = elements.get(0);
            return element.text();
        }
        return null;
    }

    protected List<Element> getElementsMatchingSelector(Document doc, String selector) {
        List<Element> elementsList = new ArrayList<>();
        elementsList.addAll(doc.select(selector));
        return elementsList;
    }

    protected Element getFirstElementMatchingSelector(Document doc, String selector) {
        return doc.select(selector).first();
    }


    protected String parseTitle(Document doc) {
        return getStringValueOfFirstElementWithSelector(doc, DEFAULT_TITLE_SELECTOR);
    }

    protected String parseDescription(Document doc) {
        return getStringValueOfFirstElementWithSelector(doc, DEFAULT_DESCRIPTION_SELECTOR);
    }

    protected abstract ParsedProduct parseResource(File file, Document doc, JobConfiguration jobConfiguration);

    protected ParsedProduct createParsedProductEntity(File file, JobConfiguration jobConfiguration) {
        String resourceURI = generateResourceURI(file, jobConfiguration);
        return new ParsedProduct(resourceURI);
    }

}
