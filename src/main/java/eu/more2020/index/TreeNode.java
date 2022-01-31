package eu.more2020.index;

import com.google.common.math.StatsAccumulator;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;

import java.util.*;

public class TreeNode {

    private static int counter;

    private final Integer label;

    protected List<Point> points;

    private Int2ObjectSortedMap<TreeNode> children;

    private TreeNode parent;

    private Map<Integer, StatsAccumulator> stats;

    public TreeNode(Integer label, TreeNode parent) {
        this.label = label;
        this.parent = parent;
        counter++;
    }

    public static int getInstanceCount() {
        return counter;
    }

    public void adjustStats(List<Float> values) {
        if (stats == null) {
            stats = new HashMap<>();
        }
        for(int i = 0; i < values.size(); i++) {
            StatsAccumulator s = new StatsAccumulator();
            s.add(values.get(i));
            stats.put(i, s);
        }
    }

    public boolean hasStats() {
        return stats != null;
    }

    public TreeNode addPoint(Point point) {
        if (points == null) {
            points = new ArrayList<>();
        }
        points.add(point);
        return this;
    }

    public List<Point> getPoints() {
        return points;
    }

    public Map<Integer, StatsAccumulator> getStats() {
        return stats;
    }

    public TreeNode getChild(Integer label) {
        return children != null ? children.get(label) : null;
    }

    public TreeNode getOrAddChild(Integer label) {
        if (children == null) {
            children = new Int2ObjectLinkedOpenHashMap();
        }
        TreeNode child = getChild(label);
        if (child == null) {
            child = new TreeNode(label, this);
            children.put(label, child);
        }
        return child;
    }

    public Integer getLabel() {
        return label;
    }

    public Collection<TreeNode> getChildren() {
        return children == null ? null : children.values();
    }

    public TreeNode getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "label=" + label +
//                ", parent=" + (parent != null ? parent.getLabel() : "null")+
//                ", children=" + children +
//                ", stats=" + stats +
                '}';
    }

    public void convertToNonleaf() {
        points = null;
        stats = null;
    }

}
