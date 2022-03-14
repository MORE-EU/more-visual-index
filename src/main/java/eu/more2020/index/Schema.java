package eu.more2020.index;

import com.univocity.parsers.csv.CsvParserSettings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static eu.more2020.index.config.IndexConfig.FORMAT;
import static java.time.temporal.ChronoField.*;


public class Schema {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT, Locale.ENGLISH);
    private final String csv;
    private final int timeColumn;
    private boolean hasHeader = false;
    private Character delimiter = ',';
    private List<TemporalField> temporaltHierarchy = Arrays.asList(YEAR, MONTH_OF_YEAR, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE_OF_HOUR);

    public Schema(String csv, Character delimiter, int timeColumn, boolean hasHeader) {
        this.csv = csv;
        this.delimiter = delimiter;
        this.timeColumn = timeColumn;
        this.hasHeader = hasHeader;
    }

    public static LocalDateTime parseStringToDate(String s) {
        return LocalDateTime.parse(s, formatter);
    }

    public CsvParserSettings createCsvParserSettings() {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setDelimiter(delimiter);
        parserSettings.setIgnoreLeadingWhitespaces(false);
        parserSettings.setIgnoreTrailingWhitespaces(false);
        parserSettings.setHeaderExtractionEnabled(hasHeader);
        parserSettings.setLineSeparatorDetectionEnabled(true);

        return parserSettings;
    }

    public int getTimeColumn() {
        return timeColumn;
    }

    public boolean getHasHeader() {
        return hasHeader;
    }

    public String getCsv() {
        return csv;
    }

    public List<TemporalField> getTemporaltHierarchy() {
        return temporaltHierarchy;
    }

    public void setTemporaltHierarchy(List<TemporalField> temporaltHierarchy) {
        this.temporaltHierarchy = temporaltHierarchy;
    }
}
