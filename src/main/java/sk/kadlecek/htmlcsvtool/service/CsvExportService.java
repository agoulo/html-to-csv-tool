package sk.kadlecek.htmlcsvtool.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedResourcesTable;
import sk.kadlecek.htmlcsvtool.util.DateTimeUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExportService implements ExportService {

    private static final Logger logger = LoggerFactory.getLogger(CsvExportService.class);

    private static final String OUTPUT_FILENAME_DATETIME_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    @Override
    public void export(ParsedResourcesTable parsedResourcesTable, JobConfiguration jobConfiguration) {

        String fileName = jobConfiguration.getOutputPath() +
                "output-" + DateTimeUtil.getCurrentTimestamp(OUTPUT_FILENAME_DATETIME_FORMAT) + ".csv";

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        //Create the CSVFormat object with "\n" as a record delimiter
        CSVFormat csvFileFormat = CSVFormat.DEFAULT
                .withRecordSeparator(jobConfiguration.getOutputCsvLineSeparator())
                .withDelimiter(jobConfiguration.getOutputCsvColumnSeparator())
                .withQuote(jobConfiguration.getOutputCsvEnclosedBy());

        List<String> header = parsedResourcesTable.getColumnNames();

        try {
            //initialize FileWriter object
            fileWriter = new FileWriter(fileName);

            //initialize CSVPrinter object
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

            //Create CSV file header
            csvFilePrinter.printRecord(header);

            //Write a new student object list to the CSV file
            for (List<String> row : parsedResourcesTable.getTableData()) {
                csvFilePrinter.printRecord(row);
            }

            logger.info("CSV file '{}' created successfully.", fileName);

        } catch (Exception e) {
            logger.error("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                    if (csvFilePrinter != null) {
                        csvFilePrinter.close();
                    }
                }
            } catch (IOException e) {
                logger.error("Error while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
            }
        }
    }
}
