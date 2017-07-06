package sk.kadlecek.htmlcsvtool.parser;

import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedHtmlDocument;

import java.io.File;
import java.io.IOException;

public interface GenericParser {

    /**
     * Parses the given file.
     * @param file
     * @return
     * @throws IOException if there is an IO related error with given file.
     */
    ParsedHtmlDocument parse(File file, JobConfiguration jobCOnfiguration) throws IOException;

}
