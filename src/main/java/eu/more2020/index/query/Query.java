package eu.more2020.index.query;

import eu.more2020.index.Range;

public class Query {

    private Range range;

    private Integer measure;

    private String frequency;

    public Query(Range range, Integer measure, String frequency) {
        this.range = range;
        this.measure = measure;
        this.frequency = frequency;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getMeasure() {
        return measure;
    }

    public void setMeasure(Integer measure) {
        this.measure = measure;
    }

    @Override
    public String toString() {
        return "Query{" +
                "range=" + range +
                ", measure=" + measure +
                ", frequency='" + frequency + '\'' +
                '}';
    }
}
