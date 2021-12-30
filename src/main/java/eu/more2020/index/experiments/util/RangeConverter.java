package eu.more2020.index.experiments.util;

import com.beust.jcommander.IStringConverter;
import eu.more2020.index.Range;

public class RangeConverter implements IStringConverter<Range> {

    @Override
    public Range convert(String s) {
        return QueryUtils.convertToRange(s);
    }
}
