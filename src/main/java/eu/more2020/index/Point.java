package eu.more2020.index;

import java.time.LocalDateTime;
import java.util.Date;

public class Point {

    private LocalDateTime time;
    private long fileOffset;

    public Point(LocalDateTime time, long fileOffset) {
        this.time = time;
        this.fileOffset = fileOffset;
    }

    public long getFileOffset() {
        return fileOffset;
    }

    public void setFileOffset(long fileOffset) {
        this.fileOffset = fileOffset;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime t) {
        this.time = t;
    }

    @Override
    public String toString() {
        return "Point{" +
                "t=" + time +
                ", fileOffset=" + fileOffset +
                '}';
    }
}
