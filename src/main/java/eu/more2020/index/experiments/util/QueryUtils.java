package eu.more2020.index.experiments.util;

import eu.more2020.index.Schema;

import java.time.LocalDateTime;

public class QueryUtils {


    public static LocalDateTime convertToDate(String s)  {
        String[] ranges = s.split(",");
        return Schema.parseStringToDate(s);
    }
}
