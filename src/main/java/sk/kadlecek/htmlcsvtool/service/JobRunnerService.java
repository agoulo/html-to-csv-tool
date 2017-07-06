package sk.kadlecek.htmlcsvtool.service;

import sk.kadlecek.htmlcsvtool.bean.ApplicationConfiguration;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedHtmlDocumentsBatch;

import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;

public interface JobRunnerService {

    /**
     * Runs the job according to given jobConfiguration.
     * @param jobConfiguration
     * @throws IOException when IO error occurs
     */
    ParsedHtmlDocumentsBatch runJob(ApplicationConfiguration applicationConfiguration, JobConfiguration jobConfiguration)
            throws IOException, InterruptedException, RejectedExecutionException;

}
