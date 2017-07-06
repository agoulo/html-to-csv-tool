package sk.kadlecek.htmlcsvtool.service;

import sk.kadlecek.htmlcsvtool.bean.JobConfiguration;
import sk.kadlecek.htmlcsvtool.bean.ParsedResourcesTable;

public interface ExportService {

    void export(ParsedResourcesTable parsedResourcesTable, JobConfiguration jobConfiguration);
}
