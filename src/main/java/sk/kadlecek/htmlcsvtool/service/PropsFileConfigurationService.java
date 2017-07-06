package sk.kadlecek.htmlcsvtool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.kadlecek.htmlcsvtool.bean.ApplicationConfiguration;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.constant.ApplicationConfigurationConstant;
import sk.kadlecek.htmlcsvtool.constant.JobConfigurationConstant;
import sk.kadlecek.htmlcsvtool.enumeration.WebsiteType;
import sk.kadlecek.htmlcsvtool.util.MachineUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsFileConfigurationService implements ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(PropsFileConfigurationService.class);

    private final int INT_UNLIMITED_VALUE = -1;

    @Override
    public ApplicationConfiguration loadApplicationConfiguration(String filename) throws IOException {
        try {
            Properties applicationProperties = parsePropertiesFile(new File(filename));
            return getApplicationConfiguration(applicationProperties);
        }catch (IOException e) {
            logger.error("There was a problem with application configuration file {}: {}", filename, e.getMessage());
            throw e;
        }
    }

    @Override
    public JobConfiguration loadJobConfigurationFile(String filename) throws IOException {
        try {
            Properties jobProps = parsePropertiesFile(new File(filename));
            return getJobConfiguration(jobProps);
        } catch (IOException e) {
            logger.error("There was a problem with job configuration file {}: {}", filename, e.getMessage());
            throw e;
        }
    }

    private Properties parsePropertiesFile(File file) throws IOException {
        InputStream input = null;
        try {
            Properties props = new Properties();
            input = new FileInputStream(file.getAbsolutePath());
            props.load(input);
            return props;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    private JobConfiguration getJobConfiguration(Properties props) {
        JobConfiguration jc = new JobConfiguration();
        jc.setFilesPath(getConfigurationString(props, JobConfigurationConstant.FILES_PATH));
        jc.setOutputPath(getConfigurationString(props, JobConfigurationConstant.OUTPUT_PATH));
        jc.setWebsiteType(WebsiteType.valueOf(getConfigurationString(props, JobConfigurationConstant.WEBSITE_TYPE)));
        jc.setUriPrefix(getConfigurationString(props, JobConfigurationConstant.URI_PREFIX));

        jc.setOutputCsvLineSeparator(getConfigurationString(props, JobConfigurationConstant.OUTPUT_CSV_LINE_SEPARATOR));
        jc.setOutputCsvColumnSeparator(getConfigurationChar(props, JobConfigurationConstant.OUTPUT_CSV_COLUMN_SEPARATOR));
        jc.setOutputCsvEnclosedBy(getConfigurationChar(props, JobConfigurationConstant.OUTPUT_CSV_ENCLOSE_BY));
        return jc;
    }

    private ApplicationConfiguration getApplicationConfiguration(Properties props) {
        Integer maxConcurrentThreads = getConfigurationInteger(props, ApplicationConfigurationConstant.MAX_CONCURRENT_THREADS);
        maxConcurrentThreads = (maxConcurrentThreads != INT_UNLIMITED_VALUE) ? maxConcurrentThreads : MachineUtil.getNumberOfCpuCores();

        Integer maxFilesToProcessPerThread = getConfigurationInteger(props, ApplicationConfigurationConstant.MAX_FILES_TO_PROCESS_PER_THREAD);
        if (maxFilesToProcessPerThread < 0) {
            throw new IllegalArgumentException(ApplicationConfigurationConstant.MAX_FILES_TO_PROCESS_PER_THREAD + " needs to be a positive integer.");
        }

        return new ApplicationConfiguration(maxConcurrentThreads, maxFilesToProcessPerThread);
    }

    private String getConfigurationString(Properties props, String key) throws NullPointerException {
        if (props.getProperty(key) != null) {
            return props.getProperty(key);
        }else {
            throw new NullPointerException("Configuration value '" + key + "' not found!");
        }
    }

    private int getConfigurationInteger(Properties props, String key) throws NullPointerException, IllegalArgumentException {
        String strValue = getConfigurationString(props, key);
        try {
            return Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Configuration value '" + key + "' is invalid (int expected)!");
        }
    }

    private char getConfigurationChar(Properties props, String key) throws NullPointerException, IllegalArgumentException {
        String strValue = getConfigurationString(props, key);
        if (!strValue.isEmpty()) {
            return strValue.charAt(0);
        }else {
            throw new IllegalArgumentException("Configuration value '" + key + "' is invalid (cannot be empty string expected)!");
        }

    }

}
