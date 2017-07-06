package sk.kadlecek.htmlcsvtool.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedResourcesTable {

    private List<List<String>> tableCells = new ArrayList<>();

    private Map<String, Integer> columnIndexes = new HashMap<>();
    private List<String> columnNames = new ArrayList<>();
    private int lastColumnIndex = -1;

    private final String RESOURCE_URI_COLUMN_NAME = "Resource URI";
    private int resourceUriColumnIndex = -1;


    public ParsedResourcesTable(List<ParsedResource> parsedResources) {
        super();
        // add column for resource URIs
        resourceUriColumnIndex = addColumnIfNecessary(RESOURCE_URI_COLUMN_NAME);
        addParsedResources(parsedResources);
    }

    public void addParsedResources(List<ParsedResource> parsedResources) {
        for (ParsedResource parsedResource : parsedResources) {
            addParsedResource(parsedResource);
        }
    }

    public void addParsedResource(ParsedResource parsedResource) {
        if (parsedResource != null) {

            // add new row
            int rowIndex = addNewRow();
            setCellValue(rowIndex, resourceUriColumnIndex, parsedResource.getResourceURI());
            for (Map.Entry<String, Map<String, String>> propertyCategories : parsedResource.getPropertyCategories().entrySet()) {

                String categoryName = propertyCategories.getKey();

                for (Map.Entry<String, String> entry : propertyCategories.getValue().entrySet()) {
                    // get index of column with same property name
                    int columnIndex = addColumnIfNecessary(generateColumnName(categoryName, entry.getKey()));
                    setCellValue(rowIndex, columnIndex, entry.getValue());
                }
            }
        }
    }

    private String generateColumnName(String categoryName, String propertyName) {
        String columnName = categoryName + "." + propertyName;
        return columnName.replace(" ", "");
    }

    private int addColumnIfNecessary(String columnName) {
        if (!columnIndexes.containsKey(columnName)) {
            columnIndexes.put(columnName, increaseAndGetLastColumnIndex());
            // resize all rows
            addColumnToAllRows();
            addColumnToColumnNamesList(columnName);
        }
        return columnIndexes.get(columnName);
    }

    private int increaseAndGetLastColumnIndex() {
        return ++lastColumnIndex;
    }

    private int getNumberOfColumns() {
        return lastColumnIndex + 1;
    }

    private int addNewRow() {
        List<String> newRow = new ArrayList<>(getNumberOfColumns() + 10);
        initNewRow(newRow);
        tableCells.add(newRow);
        return tableCells.size() - 1;
    }

    private void initNewRow(List<String> newRow) {
        for (int i = 0; i < getNumberOfColumns(); i++) {
            newRow.add(null);
        }
    }

    private void setCellValue(int rowIndex, int columnIndex, String valueToSet) throws ArrayIndexOutOfBoundsException {
        List<String> row = tableCells.get(rowIndex);
        row.set(columnIndex, valueToSet);
    }

    private void addColumnToAllRows() {
        for (List<String> row : tableCells) {
            row.add(null);
        }
    }

    public List<List<String>> getTableData() {
        return this.tableCells;
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    private void addColumnToColumnNamesList(String columnName) {
        getColumnNames().add(columnName);
    }
}
