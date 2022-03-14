package eu.more2020.index;

import com.google.common.math.StatsAccumulator;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TreeNode {


    private final int label;

    private final int level;


    private long fileOffsetStart;

    private int dataPointCount = 0;


    private Int2ObjectSortedMap<TreeNode> children;

    private Map<Integer, StatsAccumulator> stats;

    public TreeNode(int label, int level) {
        this.label = label;
        this.level = level;
    }


    public void adjustStats(String[] row, Schema schema) {
        if (stats == null) {
            stats = new HashMap<>();
        }
        for (int colIndex = 0; colIndex < row.length; colIndex++) {
            if (colIndex != schema.getTimeColumn()) {
                StatsAccumulator statsAcc = stats.computeIfAbsent(colIndex, i -> new StatsAccumulator());
                statsAcc.add(Double.parseDouble(row[colIndex]));
            }
        }
    }

    public boolean hasStats() {
        return stats != null;
    }

    public long getFileOffsetStart() {
        return fileOffsetStart;
    }

    public void setFileOffsetStart(long fileOffsetStart) {
        this.fileOffsetStart = fileOffsetStart;
    }

    public int getDataPointCount() {
        return dataPointCount;
    }

    public void setDataPointCount(int dataPointCount) {
        this.dataPointCount = dataPointCount;
    }

    public Map<Integer, StatsAccumulator> getStats() {
        return stats;
    }

    public TreeNode getChild(Integer label) {
        return children != null ? children.get(label) : null;
    }

    public TreeNode getOrAddChild(int label) {
        if (children == null) {
            children = new Int2ObjectLinkedOpenHashMap();
        }
        TreeNode child = getChild(label);
        if (child == null) {
            child = new TreeNode(label, level + 1);
            children.put(label, child);
        }
        return child;
    }

    public int getLabel() {
        return label;
    }

    public Collection<TreeNode> getChildren() {
        return children == null ? null : children.values();
    }

    public int getLevel() {
        return level;
    }


    @Override
    public String toString() {
        return "TreeNode{" +
                "label=" + label +
                ", level=" + level +
                ", fileOffsetStart=" + fileOffsetStart +
                ", dataPointCount=" + dataPointCount +
                ", stats = " + (stats == null ? null : "{" + stats.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue().mean()).collect(Collectors.joining(", ")) + "}") +
                "}";
    }
}
