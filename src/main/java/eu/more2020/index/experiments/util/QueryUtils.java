package eu.more2020.index.experiments.util;

import eu.more2020.index.Range;
import eu.more2020.index.Schema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QueryUtils {


    public static Range convertToRange(String s)  {
        String[] ranges = s.split(",");
        String from = ranges[0].split("-")[0];
        String to = ranges[1].split("-")[1];
        return new Range(Schema.parseStringToDate(from),  Schema.parseStringToDate(to));
    }
}
