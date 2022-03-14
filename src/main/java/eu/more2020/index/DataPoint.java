package eu.more2020.index;

import java.time.LocalDateTime;

public class DataPoint {

    private LocalDateTime timestamp;

    private double value;

    public DataPoint(LocalDateTime timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
