package eu.more2020.index;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Range implements Serializable {

    private final LocalDateTime from;
    private final LocalDateTime to;

    public Range(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;

    }
    public LocalDateTime getFrom() {
        return from;
    }
    public LocalDateTime getTo() {
        return to;
    }

    public boolean contains(LocalDateTime x) {
        return from.isBefore(x) && to.isAfter(x);
    }

    public boolean intersects(Range other) {
        return (this.from.isBefore(other.to) && this.to.isAfter(other.from));
    }

    public boolean encloses(Range other) {
        return (this.from.isBefore(other.from) && this.to.isAfter(other.to));
    }


    public float getSize() {
        return to.getNano() - from.getNano();
    }


    public List toList() {
        List<LocalDateTime> list = new ArrayList<>(2);
        list.add(this.from);
        list.add(this.to);
        return list;
    }

    public double distanceFrom(Range other){
        return 0.0;
    }


    @Override
    public String toString() {
        return from.toString() + "," + to.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return Objects.equals(from, range.from) &&
                Objects.equals(to, range.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
