package eu.more2020.index.query;

import eu.more2020.index.Range;

public class Query {

    private Range range;
    private Integer frequency;

    public Query(Range range, Integer frequency) {
        this.range = range;
        this.frequency = frequency;
    }

    public Range getRange() {
        return range;
    }

    public Integer getFrequency() {
        return frequency;
    }
}
