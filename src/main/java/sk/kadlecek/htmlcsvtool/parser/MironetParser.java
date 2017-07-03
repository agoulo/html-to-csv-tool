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
import java.util.Map;

/**
 * An parser implementation for the mironet.cz HTML documents.
 */
public class MironetParser extends AbstractParser {

    public static final Logger logger = LoggerFactory.getLogger(MironetParser.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

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

    private String cleanTitle(String title) {
        final String titlePostfix = " | Mironet.cz";
        return title.substring(0, title.indexOf(titlePostfix));
    }

    private ParsedProduct parseProduct(Document doc, ParsedProduct parsedProduct) {
        String productTitle = getStringValueOfFirstElementWithSelector(doc, "div.product_detail div.product_name h1");
        parsedProduct.setTitleProperty(productTitle);

        String productPrice = getStringValueOfFirstElementWithSelector(doc, "div.product_detail div.product_cena span.product_dph");
        parsedProduct.setPriceProperty(cleanPrice(productPrice));

        Element paramsTable = getFirstElementMatchingSelector(doc, "div#parametry table.paramTblFix tbody");
        if (paramsTable != null) {
            Map<String, Map<String, String>> mapCatParams = parseParamsTable(paramsTable);
            mergeCategoryParamsMapToParsedProduct(parsedProduct, mapCatParams);
        }

        return parsedProduct;
    }

    private void mergeCategoryParamsMapToParsedProduct(ParsedProduct parsedProduct, Map<String, Map<String, String>> categoryParamsMap) {
        for (Map.Entry<String, Map<String, String>> paramsCat : categoryParamsMap.entrySet()) {
            if (paramsCat.getKey() == null) {
                parsedProduct.addRootProperties(paramsCat.getValue());
            } else {
                parsedProduct.addProperties(paramsCat.getKey(), paramsCat.getValue());
            }
        }
    }

    private String cleanPrice(String price) {
        if (price != null) {
            final String pricePostfix = ",-";
            final String priceCurrency = " Kč";
            return price.replace(pricePostfix, priceCurrency);
        }
        return null;
    }

    private Map<String, Map<String, String>> parseParamsTable(Element paramsTable) {
        Map<String, Map<String, String>> mapParamCategories = new HashMap<>();

        Elements rows = paramsTable.getElementsByTag("tr");

        String categoryName = null;
        Map<String, String> paramsMap = new HashMap<>();

        for (Element row : rows) {
            Elements cols = row.getElementsByTag("td");

            if (cols.size() == 1) {
                Element col = cols.first();
                if (col.className().equals("ParamItemHdr")) {
                    categoryName = col.text();
                    mapParamCategories.put(categoryName, paramsMap);
                } else if (col.className().equals("ParamItemHdrSep")) {
                    paramsMap = new HashMap<>();
                }
            } else if (cols.size() == 2) {
                paramsMap.put(cols.first().text(), cols.last().text());
            }
        }
        return mapParamCategories;
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
