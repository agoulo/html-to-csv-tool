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

public class CeskyMobilParser extends AbstractParser {

    public static final Logger logger = LoggerFactory.getLogger(CeskyMobilParser.class);

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
        String productTitle = getStringValueOfFirstElementWithSelector(doc, "div#mainWrapper h1");
        parsedProduct.setTitleProperty(productTitle);

        String productPrice = getStringValueOfFirstElementWithSelector(doc, "div.productBuybox span.priceValue");
        String productCurrency = getStringValueOfFirstElementWithSelector(doc, "div.productBuybox span.currency");
        parsedProduct.setPriceProperty(productPrice + " " + productCurrency);

        Element paramsList = getFirstElementMatchingSelector(doc, "div.tabWrapper.technicalTab ul.properties");
        if (paramsList != null) {
            Map<String, String> paramsMap = parseParamsList(paramsList);
            parsedProduct.addRootProperties(paramsMap);
        }

        return parsedProduct;
    }

    /**
     * Parse list of parameters. Parameters are not divided into categories.
     * @param paramsList
     * @return
     */
    private Map<String, String> parseParamsList(Element paramsList) {
        Map<String, String> paramsMap = new HashMap<>();

        Elements rows = paramsList.children();
        for (Element row: rows) {
            String paramName = row.getElementsByTag("span").first().text();
            String paramValue = row.ownText();
            paramsMap.put(paramName, paramValue);
        }
        return paramsMap;

    }
}
