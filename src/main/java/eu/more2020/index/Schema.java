package eu.more2020.index;

import com.univocity.parsers.csv.CsvParserSettings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static eu.more2020.index.config.IndexConfig.FORMAT;


public class Schema {
    private final String csv;
    private boolean hasHeader = false;
    private final int timeColumn;
    private Character delimiter = ',';

    private static final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern(FORMAT, Locale.ENGLISH);

    public static LocalDateTime parseStringToDate(String s){
        return LocalDateTime.parse(s, formatter);
    }

    public CsvParserSettings createCsvParserSettings() {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setDelimiter(delimiter);
        parserSettings.setIgnoreLeadingWhitespaces(false);
        parserSettings.setIgnoreTrailingWhitespaces(false);
        parserSettings.setHeaderExtractionEnabled(hasHeader);

        return parserSettings;
    }

    public int getTimeColumn(){
        return timeColumn;
    }
    public boolean getHasHeader(){
        return hasHeader;
    }

    public String getCsv(){
        return csv;
    }

    public Schema(String csv, Character delimiter, int timeColumn) {
        this.csv = csv;
        this.delimiter = delimiter;
        this.timeColumn = timeColumn;

    }

}
