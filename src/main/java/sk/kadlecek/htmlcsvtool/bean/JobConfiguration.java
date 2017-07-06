package sk.kadlecek.htmlcsvtool.bean;

import sk.kadlecek.htmlcsvtool.enumeration.WebsiteType;

/**
 * A bean class that describes the configuration for parsing HTML files from a given website type.
 */
public class JobConfiguration {

    /**
     * Absolute path to the folder containing HTML files.
     */
    private String filesPath;

    /**
     * Type of the website to process.
     */
    private WebsiteType websiteType;

    /**
     * The URI will be created from the relative path to file based on the filesPath. If some prefix
     * should be appended, it can be configured here.
     */
    private String uriPrefix;

    /**
     * Path, where to save the output files.
     */
    private String outputPath;

    private String outputCsvLineSeparator;
    private char outputCsvColumnSeparator;
    private char outputCsvEnclosedBy;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("JobConfiguration: \n");
        sb.append("\tFiles Path: " + filesPath + "\n");
        sb.append("\tWebsite Type: " + websiteType + "\n");
        sb.append("\tOutput Path: " + outputPath + "\n");
        sb.append("\tURI Prefix: " + uriPrefix + "\n");

        sb.append("\tOutput CSV Line Separator: " + outputCsvLineSeparator + "\n");
        sb.append("\tOutput CSV Column Separator: " + outputCsvColumnSeparator + "\n");
        sb.append("\tOutput CSV Enclosed By: " + outputCsvEnclosedBy + "\n");

        return sb.toString();
    }

    public String getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    public WebsiteType getWebsiteType() {
        return websiteType;
    }

    public void setWebsiteType(WebsiteType websiteType) {
        this.websiteType = websiteType;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public String getOutputCsvLineSeparator() {
        return outputCsvLineSeparator;
    }

    public void setOutputCsvLineSeparator(String outputCsvLineSeparator) {
        this.outputCsvLineSeparator = outputCsvLineSeparator;
    }

    public char getOutputCsvColumnSeparator() {
        return outputCsvColumnSeparator;
    }

    public void setOutputCsvColumnSeparator(char outputCsvColumnSeparator) {
        this.outputCsvColumnSeparator = outputCsvColumnSeparator;
    }

    public char getOutputCsvEnclosedBy() {
        return outputCsvEnclosedBy;
    }

    public void setOutputCsvEnclosedBy(char outputCsvEnclosedBy) {
        this.outputCsvEnclosedBy = outputCsvEnclosedBy;
    }
}
