package sk.kadlecek.htmlcsvtool.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Description of a resource parsed from a HTML document.
 */
public class ParsedResource {

    private static final String ROOT_CATEGORY = "root";

    /**
     * The URI of a parsed resource.
     */
    private String resourceURI;

    private Map<String, Map<String, String>> categorizedProperties = new HashMap<>();

    public ParsedResource(String resourceURI) {
        super();
        this.resourceURI = resourceURI;
    }


    public String getResourceURI() {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    public void addProperty(String category, String property, String value) {
        if (!categorizedProperties.containsKey(category)) {
            categorizedProperties.put(category, new HashMap<>());
        }
        categorizedProperties.get(category).put(property, normalizePropertyValue(value));
    }

    public void addProperties(String category, Map<String, String> propertiesMap) {
        if (!categorizedProperties.containsKey(category)) {
            categorizedProperties.put(category, new HashMap<>());
        }
        Map<String, String> categoryMap = categorizedProperties.get(category);
        for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
            categoryMap.put(entry.getKey(), normalizePropertyValue(entry.getValue()));
        }
    }

    public void addRootProperties(Map<String, String> propertiesMap) {
        addProperties(ROOT_CATEGORY, propertiesMap);
    }
    /**
     * Adds a new property with value to the ROOT category of properties.
     * @param property
     * @param value
     */
    public void addRootProperty(String property, String value) {
        addProperty(ROOT_CATEGORY, property, value);
    }

    public Map<String, Map<String, String>> getPropertyCategories() {
        return categorizedProperties;
    }

    private String normalizePropertyValue(String value) {
        if (value != null) {
            return value.toLowerCase();
        }
        return value;
    }
}
