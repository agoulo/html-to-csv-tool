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

public class DebenhamsPlusParser extends AbstractParser {

    public static final Logger logger = LoggerFactory.getLogger(DebenhamsPlusParser.class);

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

    private ParsedProduct parseProduct(Document doc, ParsedProduct parsedProduct) {
        String productTitle = getStringValueOfFirstElementWithSelector(doc, "div#ProductTitle h1 span.title");
        parsedProduct.setTitleProperty(productTitle);

        String productPrice = parsePrice(doc);
        parsedProduct.setPriceProperty(productPrice);

        Map<String, Map<String, String>> mapCatParams = parseSpecifications(doc);
        mergeCategoryParamsMapToParsedProduct(parsedProduct, mapCatParams);
        return parsedProduct;
    }

    private String parsePrice(Document doc) {
        Element elem = getFirstElementMatchingSelector(doc, "div#productpagetop div.ProductPrice img");
        if (elem != null) {
            return elem.attr("alt");
        }
        return null;
    }

    private Map<String, Map<String, String>> parseSpecifications(Document doc) {
        Map<String, Map<String, String>> specsMap = new HashMap<>();

        Element specsTable = getFirstElementMatchingSelector(doc, "div#productpagebottom div#specs div#specData table#gvwSpec tbody");
        if (specsTable != null) {
            Elements rows = specsTable.children();

            Map<String, String> paramsMap = new HashMap<>();
            for (Element row : rows) {

                if (row.children().size() == 1) {
                    // category heading
                    Element col = row.children().get(0);
                    String catName = col.text().trim();
                    if (!specsMap.containsKey(catName)) {
                        specsMap.put(catName, new HashMap<>());
                    }
                    paramsMap = specsMap.get(catName);

                }else if (row.children().size() == 2) {
                    // param name, value pair
                    Elements cols = row.children();
                    String paramName = cols.get(0).text().trim();
                    String paramValue = cols.get(1).text().trim();
                    paramsMap.put(paramName, paramValue);
                }
            }
        }

        return specsMap;
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
}
