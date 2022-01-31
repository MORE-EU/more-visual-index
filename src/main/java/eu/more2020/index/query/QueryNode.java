package eu.more2020.index.query;

import eu.more2020.index.NodePointsIterator;
import eu.more2020.index.Point;
import eu.more2020.index.TreeNode;

import java.util.Iterator;


public class QueryNode implements Iterable<Point> {

    private TreeNode node;


    public QueryNode(TreeNode node) {
        this.node = node;

    }


    @Override
    public Iterator<Point> iterator() {
        return new NodePointsIterator(this);
    }

    @Override
    public String toString() {
        return "QueryNode{" +
                "node=" + node +
                '}';
    }
}