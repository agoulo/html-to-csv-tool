package sk.kadlecek.htmlcsvtool.service;

import sk.kadlecek.htmlcsvtool.bean.ApplicationConfiguration;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;

import java.io.IOException;

public interface ConfigurationService {

    ApplicationConfiguration loadApplicationConfiguration(String filename) throws IOException;

    JobConfiguration loadJobConfigurationFile(String filename) throws IOException;
}
