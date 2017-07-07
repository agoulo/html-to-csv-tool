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
 * An parser implementation for the czc.cz HTML documents.
 */
public class CzcParser extends AbstractParser {

    public static final Logger logger = LoggerFactory.getLogger(CzcParser.class);

    @Override
    protected ParsedProduct parseResource(File file, Document doc, JobConfiguration jobConfiguration) {
        ParsedProduct parsedProduct = createParsedProductEntity(file, jobConfiguration);
        parseProduct(doc, parsedProduct);
        return parsedProduct;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    private ParsedProduct parseProduct(Document doc, ParsedProduct parsedProduct) {
        String productTitle = getStringValueOfFirstElementWithSelector(doc, "div#product-detail h1 span.data_title");
        parsedProduct.setTitleProperty(productTitle);

        String productPrice = getStringValueOfFirstElementWithSelector(doc, "div#product-detail div.total-price span.price");
        parsedProduct.setPriceProperty(productPrice);

        Element paramsTable = getFirstElementMatchingSelector(doc, "div#pd-parameter div.sp-content");
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

    private Map<String, Map<String, String>> parseParamsTable(Element paramsTable) {
        Map<String, Map<String, String>> mapParamCategories = new HashMap<>();

        Elements rows = paramsTable.children();
        boolean ignoreHeading = true;

        String categoryName = null;
        Map<String, String> paramsMap = null;

        for (Element row: rows) {
            //ignore first few rows until first h3 element
            if (row.tagName().equals("h3")) {
                ignoreHeading = false;
                categoryName = row.text();
                paramsMap = new HashMap<>();
                mapParamCategories.put(categoryName, paramsMap);
            } else {
                if (!ignoreHeading) {
                    // is a div with name/value, can contain multiple rows
                    Elements paramRows = row.getElementsByTag("p");
                    for (Element paramRow : paramRows) {
                        Element paramNameElement = paramRow.getElementsByTag("span").first();
                        Element paramValueElement = paramRow.getElementsByTag("strong").first();

                        if (paramNameElement != null && paramValueElement != null) {
                            String paramName = removeColonCharFromParamName(paramNameElement.text());
                            String paramValue = correctYesNoAnswer(paramName, paramValueElement.text());
                            paramsMap.put(paramName, paramValue);
                        }
                    }
                }
            }
        }
        return mapParamCategories;
    }

    private String removeColonCharFromParamName(String paramName) {
        if (paramName.endsWith(":")) {
            paramName = paramName.substring(0, paramName.length() -1);
        }
        return paramName;
    }
}
