package sk.kadlecek.htmlcsvtool.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedProduct;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MobileShopParser extends AbstractParser {

    public static final Logger logger = LoggerFactory.getLogger(MobileShopParser.class);

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
        String productTitle = getStringValueOfFirstElementWithSelector(doc, "div#MainContent section h1.heading");
        parsedProduct.setTitleProperty(productTitle);

        String productPrice = getStringValueOfFirstElementWithSelector(doc,
                "div#MainContent.main section div#productDetails div.buy div#price_0 small");
        parsedProduct.setPriceProperty(productPrice);

        Map<String, Map<String, String>> mapCatParams = parseSpecifications(doc);
        mergeCategoryParamsMapToParsedProduct(parsedProduct, mapCatParams);
        return parsedProduct;
    }

    private Map<String, Map<String, String>> parseSpecifications(Document doc) {
        Map<String, Map<String, String>> mapParamCategories = new HashMap<>();

        Element specsTable = getFirstElementMatchingSelector(doc, "div#MainContent div#specification dl");
        Elements categoryDts = specsTable.select(":root > dt");

        for (Element categoryDt : categoryDts) {
            String categoryName = removeColonCharFromParamName(categoryDt.text());
            if (!mapParamCategories.containsKey(categoryName)) {
                mapParamCategories.put(categoryName, new HashMap<String, String>());
            }
            Map<String, String> paramsMap = mapParamCategories.get(categoryName);

            Element categoryDd = categoryDt.nextElementSibling();
            Elements paramDts = categoryDd.select(":root > dl > dt");
            for (Element paramDt : paramDts) {
                String paramName = removeColonCharFromParamName(paramDt.text());

                Element paramDd = paramDt.nextElementSibling();
                String paramValue = paramDd.text();

                paramsMap.put(paramName, paramValue);
            }
        }
        return mapParamCategories;
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
