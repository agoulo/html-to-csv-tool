package sk.kadlecek.htmlcsvtool.enumeration;

import sk.kadlecek.htmlcsvtool.parser.*;

public enum WebsiteType {

    CZC (CzcParser.class),
    MIRONET (MironetParser.class),
    DATART (DatartParser.class),
    CESKYMOBIL (CeskyMobilParser.class),
    SMARTY (SmartyParser.class),
    GEARBEST (GearBestParser.class),
    MOBILESHOP (MobileShopParser.class),
    KOGAN (KoganParser.class),
    DEBENHAMSPLUS (DebenhamsPlusParser.class);

    private Class<? extends GenericParser> parserClass;

    private WebsiteType(Class<? extends GenericParser> parserClass) {
        this.parserClass = parserClass;
    }

    public Class<? extends GenericParser> getParserClass() {
        return this.parserClass;
    }

}
