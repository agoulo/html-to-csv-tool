package sk.kadlecek.htmlcsvtool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.kadlecek.htmlcsvtool.bean.ApplicationConfiguration;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedHtmlDocument;
import sk.kadlecek.htmlcsvtool.bean.ParsedHtmlDocumentsBatch;
import sk.kadlecek.htmlcsvtool.runnable.FetcherCallable;
import sk.kadlecek.htmlcsvtool.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public class DefaultJobRunnerService implements JobRunnerService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultJobRunnerService.class);

    @Override
    public ParsedHtmlDocumentsBatch runJob(ApplicationConfiguration applicationConfiguration, JobConfiguration jobConfiguration)
            throws IOException, InterruptedException, RejectedExecutionException {

        logger.info("Running job: {}", jobConfiguration.toString());
        // does location exist?
        File inputDirectory = new File(jobConfiguration.getFilesPath());
        if (FileUtil.doesDirectoryExist(inputDirectory)) {
            logger.trace("Input Directory '{}' OK.", inputDirectory);

            // collect all files in directory (and in all subdirectories),
            // remember the relative path from the input directory - as it determines the URI of the file
            List<File> allFilesToProcess =
                    FileUtil.retrieveAllFilesInDirectoryAndItsSubdirectories(inputDirectory);

            logger.info("Found total {} files to process.", allFilesToProcess.size());

            // run parser on all files (multithreaded)
            int maxConcurrentThreads = applicationConfiguration.getMaxConcurrentThreads();
            ExecutorService threadPool = Executors.newFixedThreadPool(maxConcurrentThreads);
            Set<FetcherCallable> fetcherCallables = new HashSet<>();

            // initialize threads
            int filesLeft = allFilesToProcess.size();
            int i = 0;
            while (filesLeft > 0) {
                // take subset of data to process by thread
                List<File> filesToProcess = getFilesToProcessByThread(allFilesToProcess, i,
                        applicationConfiguration.getMaxFilesToProcessPerThread());

                fetcherCallables.add(new FetcherCallable(i, filesToProcess, jobConfiguration));
                filesLeft = filesLeft - filesToProcess.size();
                i++;
            }

            List<Future<ParsedHtmlDocumentsBatch>> parsedDocumentBatchFutures;
            try {
                parsedDocumentBatchFutures = threadPool.invokeAll(fetcherCallables);
            } catch (Exception e) {
                logger.error("InvokeAll Failed: {}", e.getMessage());
                throw e;
            }

            // merge results
            ParsedHtmlDocumentsBatch allDocumentsBatch = new ParsedHtmlDocumentsBatch();

            try {
                for (Future<ParsedHtmlDocumentsBatch> future : parsedDocumentBatchFutures) {
                    for (ParsedHtmlDocument documentInBatch : future.get().getDocumentsInBatch()) {
                        allDocumentsBatch.addParsedHtmlDocumentToBatch(documentInBatch);
                    }
                }
            }catch (Exception e) {
                logger.error("{}", e);
            }

            threadPool.shutdown();

            logger.debug("Finished all threads");

            return allDocumentsBatch;
        } else {
            logger.error("Error: Input directory '{}' does not exist on the filesystem.", inputDirectory);
            throw new IOException("Input directory " + inputDirectory + " does not exist!");
        }
    }

    private static List<File> getFilesToProcessByThread(List<File> allFilesToProcess, int threadNumber, int chunkSize) {
        int totalFilesCount = allFilesToProcess.size();
        int offset = threadNumber * chunkSize;
        int toIndex = offset + chunkSize;

        if (totalFilesCount < toIndex) {
            toIndex = totalFilesCount;
        }
        return allFilesToProcess.subList(offset, toIndex);
    }

}
