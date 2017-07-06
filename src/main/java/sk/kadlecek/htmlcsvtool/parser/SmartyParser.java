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
 * An parser implementation for the smarty.cz HTML documents.
 */
public class SmartyParser extends AbstractParser {

    public static final Logger logger = LoggerFactory.getLogger(SmartyParser.class);

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
        String productTitle = getStringValueOfFirstElementWithSelector(doc, "section.content h1");
        parsedProduct.setTitleProperty(productTitle);

        String productPrice = getStringValueOfFirstElementWithSelector(doc, "section.content p.price strong");
        parsedProduct.setPriceProperty(productPrice);

        Element paramTablesList = getFirstElementMatchingSelector(doc, "section.content section.infoContent section.attribs");
        if (paramTablesList != null) {
            Map<String, Map<String, String>> mapCatParams = parseParamsTableList(paramTablesList);
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

    private Map<String, Map<String, String>> parseParamsTableList(Element paramsTableList) {
        Map<String, Map<String, String>> mapParamCategories = new HashMap<>();

        Elements rows = paramsTableList.children();

        String categoryName = null;
        Map<String, String> paramsMap = null;

        for (Element row: rows) {
            //ignore first few rows until first h3 element
            if (row.tagName().equals("h4")) {
                // category heading
                categoryName = row.text();
                paramsMap = new HashMap<>();
                mapParamCategories.put(categoryName, paramsMap);
            } else if (row.tagName().equals("div")) {
                // category params
                Elements paramRows = row.select("table tbody tr");
                for (Element paramRow : paramRows) {
                    Element paramNameElement = paramRow.select("td.l").first();
                    Element paramValueElement = paramRow.select("td.r").first();

                    if (paramNameElement != null && paramValueElement != null) {
                        String paramName = paramNameElement.text();
                        String paramValue = paramValueElement.text();
                        paramsMap.put(paramName, paramValue);
                    }
                }
            }
        }
        return mapParamCategories;
    }

}
