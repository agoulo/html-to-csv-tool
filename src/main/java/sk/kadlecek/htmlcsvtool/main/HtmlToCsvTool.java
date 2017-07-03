package sk.kadlecek.htmlcsvtool.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.kadlecek.htmlcsvtool.bean.ApplicationConfiguration;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedHtmlDocumentsBatch;
import sk.kadlecek.htmlcsvtool.constant.ApplicationConstants;
import sk.kadlecek.htmlcsvtool.service.*;

public class HtmlToCsvTool {

    private static Logger logger = LoggerFactory.getLogger(HtmlToCsvTool.class);

    /**
     * Entry point.
     * @param args
     */
    public static void main(String... args) {
        logger.info("HtmlToCsvTool started.");
        String jobConfigFileName = getJobConfigurationFileName(args);
        logger.info("Loading configuration from file: " + jobConfigFileName);
        try {
            // init services
            ConfigurationService configurationService = new PropsFileConfigurationService();
            JobRunnerService jobRunnerService = new DefaultJobRunnerService();
            ExportService exportService = new CsvExportService();

            logger.info("Loading application configuration from file: {}", ApplicationConstants.APP_CONFIG_FILENAME);
            ApplicationConfiguration applicationConfig = configurationService.loadApplicationConfiguration(ApplicationConstants.APP_CONFIG_FILENAME);

            logger.info("Loading configuration from file: {}", jobConfigFileName);
            JobConfiguration jobConfig = configurationService.loadJobConfigurationFile(jobConfigFileName);

            logger.info("Starting job..");
            ParsedHtmlDocumentsBatch parsedDocuments = jobRunnerService.runJob(applicationConfig, jobConfig);

            logger.info("Exporting results.");
            exportService.export(parsedDocuments.getParsedResourcesTable(), jobConfig);

            logger.info("Done.");
        }catch (Exception e) {
            logger.error("HtmlToCsvTool failed: {}", e.getMessage());
        }
    }

    /**
     * Retrieves the name of configuration file given in args. If no configuration filename has been provided,
     * throws exception.
     */
    private static String getJobConfigurationFileName(String... args) {
        if (args.length > 0) {
            return args[0];
        }else {
          throw new IllegalArgumentException("No job configuration given.");
        }
    }
}
