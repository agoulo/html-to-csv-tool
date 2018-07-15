package sk.kadlecek.htmlcsvtool.enumeration;

import sk.kadlecek.htmlcsvtool.parser.*;

public enum WebsiteType {

    GEARBEST (GearBestParser.class),
    MOBILESHOP (MobileShopParser.class),
    DEBENHAMSPLUS (DebenhamsPlusParser.class);

    private Class<? extends GenericParser> parserClass;

    private WebsiteType(Class<? extends GenericParser> parserClass) {
        this.parserClass = parserClass;
    }

    public Class<? extends GenericParser> getParserClass() {
        return this.parserClass;
    }

}
