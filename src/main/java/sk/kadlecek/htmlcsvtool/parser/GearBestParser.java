package sk.kadlecek.htmlcsvtool.parser;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedProduct;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GearBestParser extends AbstractParser {

    public static final Logger logger = LoggerFactory.getLogger(GearBestParser.class);

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
        String productTitle = getStringValueOfFirstElementWithSelector(doc, "div#mainWrap div.goods_info div.goods_info_inner h1");
        parsedProduct.setTitleProperty(productTitle);

        String productPrice = parsePrice(doc);
        parsedProduct.setPriceProperty(productPrice);

        Map<String, Map<String, String>> mapCatParams = parseSpecifications(doc);
        mergeCategoryParamsMapToParsedProduct(parsedProduct, mapCatParams);
        return parsedProduct;
    }

    private String parsePrice(Document doc) {
        String[] priceSelectors = {
                "div#mainWrap div.goods_info div.goods_info_inner div.goods_price span#unit_price",
                "div#mainWrap div.goods_info div.goods_info_inner p.goods_price span#unit_price",
                "div#mainWrap div.goods_info div.goods_info_inner p.price_area b#unit_price",
        };
        for (String selector : priceSelectors) {
            String price = getStringValueOfFirstElementWithSelector(doc, selector);
            if (price != null) {
                return price;
            }
        }
        return null;
    }

    private Map<String, Map<String, String>> parseSpecifications(Document doc) {
        String[] specsTableSelectors = {
                "div.detail_main div.detailShow div.product_pz div.product_pz_info table"
        };
        for (String selector : specsTableSelectors) {
            Element specsTable = getFirstElementMatchingSelector(doc, selector);
            if (specsTable != null) {
                if (specsTable.select("img").size() > 0) {
                    return parseDetailedTable(specsTable);
                }else {
                    return parseSimpleTable(specsTable);
                }
            }
        }
        return new HashMap<>();
    }

    private Map<String, Map<String, String>> parseSimpleTable(Element paramsTable) {
        Map<String, Map<String, String>> mapParamCategories = new HashMap<>();

        Elements rows = paramsTable.select("tr");

        for (Element row : rows) {
            // each row consists of 2 columns, first columns contains category name, second one parameter list
            Element catNameCol = getFirstElementMatchingSelector(row, "th");
            String categoryName = getStringValueOfFirstElementWithSelector(catNameCol, "p");

            Element catParamsCol = getFirstElementMatchingSelector(row, "td");
            List<TextNode> paramsList = catParamsCol.textNodes();
            for (TextNode paramStr : paramsList) {
                String[] splitted = paramStr.text().split(": ");

                if (!mapParamCategories.containsKey(categoryName)) {
                    mapParamCategories.put(categoryName, new HashMap<>());
                }
                Map<String, String> paramsMap = mapParamCategories.get(categoryName);
                paramsMap.put(splitted[0], splitted[1]);
            }
        }
        return mapParamCategories;
    }

    private Map<String, Map<String, String>> parseDetailedTable(Element paramsTable) {
        Map<String, Map<String, String>> mapParamCategories = new HashMap<>();

        Elements rows = paramsTable.select("tr");

        for (Element row : rows) {
            // each row consists of multiple columns, each column represents a params category
            for (Element column : row.children()) {
                String categoryName = getStringValueOfFirstElementWithSelector(column, "div.product_pz_img p");

                //List<TextNode> paramsList = getFirstElementMatchingSelector(column, "p").textNodes();
                List<TextNode> paramsList = column.child(1).textNodes();
                for (TextNode paramStr : paramsList) {
                    String[] splitted = paramStr.text().split(": ");

                    if (!mapParamCategories.containsKey(categoryName)) {
                        mapParamCategories.put(categoryName, new HashMap<String, String>());
                    }
                    Map<String, String> paramsMap = mapParamCategories.get(categoryName);
                    paramsMap.put(splitted[0], splitted[1]);
                }
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
