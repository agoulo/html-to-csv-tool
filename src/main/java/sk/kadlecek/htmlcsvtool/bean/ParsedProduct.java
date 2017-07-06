package sk.kadlecek.htmlcsvtool.bean;

public class ParsedProduct extends ParsedResource {

    private static final String RESOURCE_TITLE = "title";
    private static final String RESOURCE_PRICE = "price";

    public ParsedProduct(String resourceURI) {
        super(resourceURI);
    }

    /**
     * Sets a value for Resource Title property.
     * @param value
     */
    public void setTitleProperty(String value) {
        addRootProperty(RESOURCE_TITLE, value);
    }

    public void setPriceProperty(String value) {
        addRootProperty(RESOURCE_PRICE, value);
    }

}
