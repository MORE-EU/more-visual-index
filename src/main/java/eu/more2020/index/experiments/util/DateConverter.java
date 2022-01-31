package eu.more2020.index.experiments.util;

import com.beust.jcommander.IStringConverter;

import java.time.LocalDateTime;

public class DateConverter implements IStringConverter<LocalDateTime> {

    @Override
    public LocalDateTime convert(String s) {
        return QueryUtils.convertToDate(s);
    }
}
