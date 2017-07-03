package sk.kadlecek.htmlcsvtool.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedProduct;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An parser implementation for the datart.cz HTML documents.
 */
public class DatartParser extends AbstractParser {

    private static final Logger logger = LoggerFactory.getLogger(DatartParser.class);

    @Override
    protected ParsedProduct parseResource(File file, Document doc, JobConfiguration jobConfiguration) {
        ParsedProduct parsedProduct = createParsedProductEntity(file, jobConfiguration);
        parseProduct(doc, parsedProduct);
        return parsedProduct;
    }

    @Override
    protected String parseTitle(Document doc) {
        return cleanTitle(getStringValueOfFirstElementWithSelector(doc, DEFAULT_TITLE_SELECTOR));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    private String cleanTitle(String title) {
        final String titlePrefix = "DATART | ";
        return title.substring(titlePrefix.length());
    }

    private ParsedProduct parseProduct(Document doc, ParsedProduct parsedProduct) {
        String productTitle = getStringValueOfFirstElementWithSelector(doc, "div.page-wrapper h2");
        parsedProduct.setTitleProperty(productTitle);

        String productPrice = getStringValueOfFirstElementWithSelector(doc, "div.page-wrapper p em.product-detail-price");
        parsedProduct.setPriceProperty(productPrice);

        List<Element> categoryElements = getElementsMatchingSelector(doc, "div#id-attributes table.list");
        for (Element categoryElement: categoryElements) {
            String categoryName = parseCategoryName(categoryElement);
            parsedProduct.addProperties(categoryName, parseAttributesCategory(categoryElement));
        }
        return parsedProduct;
    }

    private String parseCategoryName(Element categoryRoot) {
        return categoryRoot.getElementsByTag("caption").first().text().trim();
    }

    private Map<String, String> parseAttributesCategory(Element categoryRoot) {
        Map<String, String> attributes = new HashMap<>();
        Elements tableRows = categoryRoot.getElementsByTag("tbody").first().getElementsByTag("tr");
        for (Element trow : tableRows) {
            Elements cols = trow.getElementsByTag("td");

            String propertyName = cols.first().text().trim();
            String propertyValue = cols.last().text().trim();
            attributes.put(propertyName, propertyValue);
        }
        return attributes;
    }
}
