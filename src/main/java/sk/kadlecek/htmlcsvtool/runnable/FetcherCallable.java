package sk.kadlecek.htmlcsvtool.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedHtmlDocument;
import sk.kadlecek.htmlcsvtool.bean.ParsedHtmlDocumentsBatch;
import sk.kadlecek.htmlcsvtool.parser.GenericParser;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class FetcherCallable implements Callable<ParsedHtmlDocumentsBatch> {

    private int id;
    private List<File> filesToProcess;
    private JobConfiguration jobConfiguration;

    private static final Logger logger = LoggerFactory.getLogger(FetcherCallable.class);

    public FetcherCallable(int id, List<File> filesToProcess, JobConfiguration jobConfiguration) {
        this.id = id;
        this.filesToProcess = filesToProcess;
        this.jobConfiguration = jobConfiguration;
        logger.debug(getLogMessage("Thread created. {} files to process."), filesToProcess.size());
    }


    @Override
    public ParsedHtmlDocumentsBatch call() throws Exception {
        logger.debug(getLogMessage("Thread called."));
        Class<? extends GenericParser> parserClass = jobConfiguration.getWebsiteType().getParserClass();

        GenericParser parser;
        try {
            parser = parserClass.newInstance();
        }catch (InstantiationException | IllegalAccessException e) {
            logger.error("Failed to instantiate parser class {}: {}", parserClass.getCanonicalName(), e.getMessage());
            throw e;
        }

        ParsedHtmlDocumentsBatch result = new ParsedHtmlDocumentsBatch();
        for (File f : filesToProcess) {
            logger.debug(getLogMessage("Processing file '{}'."), f.getAbsoluteFile());
//            try {
                ParsedHtmlDocument parsedHtmlDocument = parser.parse(f, jobConfiguration);
                // merge parsed file into result
                result.addParsedHtmlDocumentToBatch(parsedHtmlDocument);
//            }catch (Exception e) {
             //noop
//            }
        }
        return result;
    }

    private String getLogMessage(String msg) {
        return "(ID: " + id + "): " + msg;
    }
}
