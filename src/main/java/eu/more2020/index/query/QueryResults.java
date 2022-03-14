package eu.more2020.index.query;

import eu.more2020.index.DataPoint;

import java.util.ArrayList;
import java.util.List;

public class QueryResults {

    private Query query;

    private List<DataPoint> dataPoints = new ArrayList<>();

    private int ioCount;

    public QueryResults(Query query) {
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public int getIoCount() {
        return ioCount;
    }

    public void setIoCount(int ioCount) {
        this.ioCount = ioCount;
    }

    @Override
    public String toString() {
        return "QueryResults{" +
                "query=" + query +
                ", dataPoints=" + dataPoints +
                '}';
    }
}
