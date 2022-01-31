package eu.more2020.index;

import com.google.common.math.StatsAccumulator;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import eu.more2020.index.query.Query;
import eu.more2020.index.query.QueryNode;
import eu.more2020.index.query.QueryResults;
import eu.more2020.index.util.io.RandomAccessReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Tree {

    protected TreeNode root;

    private Schema schema;

    private int objectsIndexed = 0;

    private RandomAccessReader randomAccessReader;

    private boolean isInitialized = false;
    public Tree(Schema schema) {
        this.schema = schema;
    }

    protected TreeNode addPoint(Point point, Stack<Integer> labels) {
        return getOrAddCategoricalNode(labels).addPoint(point);
    }

    private TreeNode getOrAddCategoricalNode(Stack<Integer> labels) {
        if (root == null) {
            root = new TreeNode((Integer) 0, null);
        }
        TreeNode node = root;
        for (Integer label : labels) {
            TreeNode child = node.getOrAddChild(label);
            node = child;
        }
        return node;
    }

    public QueryResults initialize(Query q0) {
        CsvParserSettings parserSettings = schema.createCsvParserSettings();
        parserSettings.setColumnReorderingEnabled(false);
        parserSettings.setHeaderExtractionEnabled(schema.getHasHeader());
        CsvParser parser = new CsvParser(parserSettings);
        objectsIndexed = 0;

        parser.beginParsing(new File(schema.getCsv()), Charset.forName("US-ASCII"));
        String[] row;
        long rowOffset = parser.getContext().currentChar() - 1;
        while ((row = parser.parseNext()) != null) {
            try {
                List<String> rowList = new ArrayList<String>();
                Collections.addAll(rowList, row);
                Point point = new Point(Schema.parseStringToDate(rowList.get(0)), rowOffset);
                LocalDateTime date = point.getTime();
                Stack<Integer> labels = new Stack<>();
                labels.add(date.getYear());
                labels.add(date.getMonth().getValue());
                labels.add(date.getDayOfMonth());
                labels.add(date.getHour());
                labels.add(date.getMinute());
                TreeNode node = this.addPoint(point, labels);
                if (node == null) {
                    continue;
                }
                rowList.remove(0);
                List<Float> values = new ArrayList<>();
                values = rowList.stream().map(Float::parseFloat).collect(Collectors.toList());
                node.adjustStats(values);
                objectsIndexed ++;
            } catch (Exception e) {
                //LOG.error("Problem parsing row number " + objectsIndexed + ": " + Arrays.toString(row), e);
                continue;
            } finally {
                rowOffset = parser.getContext().currentChar() - 1;
            }

        }
        parser.stopParsing();
        isInitialized = true;
        System.out.println("Indexing Complete. Total Indexed Objects:" + objectsIndexed);
//        LOG.debug("Indexing Complete. Total Indexed Objects: " + objectsIndexed);
        QueryResults queryResults = new QueryResults(q0);
        return queryResults;
    }

    public synchronized QueryResults executeQuery(Query query) throws IOException {
        if (!isInitialized) {
            return initialize(query);
        }

        QueryResults queryResults = new QueryResults(query);

        if (randomAccessReader == null) {
            randomAccessReader = RandomAccessReader.open(new File(schema.getCsv()));
        }
        List<NodePointsIterator> rawIterators = new ArrayList<>();
        List<QueryNode> nonRawNodes = new ArrayList<>();

        List<float[]> points = new ArrayList<>();
        List<TreeNode> queryNodes = new ArrayList<>();
        LocalDateTime start = query.getRange().getFrom();
        LocalDateTime end = query.getRange().getTo();
        Integer frequency =  query.getFrequency();
        int[] startLabels = getLabels(start);
        int[] endLabels = getLabels(end);
        //traverse(root);
        queryNodes = getQueryNodes(root, queryNodes, startLabels, endLabels, true, true, 0);
        System.out.println(queryNodes.size());

//        Map<Integer, StatsAccumulator> stats = getStats(queryNodes);
//        System.out.println(stats);
        return queryResults;
    }

    public Map<Integer, StatsAccumulator> getStats(List<TreeNode> queryNodes){
        Map<Integer, StatsAccumulator> stats = new HashMap<>();
        for(TreeNode node : queryNodes){
            node.getStats().forEach(
                    (key, value) ->
                            stats.merge(key, value, (v1, v2) -> {
                                v1.addAll(v2);
                                return v1;
                            }));
        }
        return stats;
    }

    public void traverse(TreeNode node){
        Collection<TreeNode> children = node.getChildren();
        if(children == null || children.isEmpty()){
            System.out.println(node);
        }
        else{
            for(TreeNode child : children){
                System.out.println(child);
                traverse(child);
            }
        }
    }

    public int getLevel(TreeNode node){
        int level = 0;
        if(node != root)
            do{
                level ++;
                node = node.getParent();
            }while(node != root);
        return level;
    }

    public int[] getLabels(LocalDateTime d){
        int[] labels = new int[5];
        labels[0] = d.getYear();
        labels[1] = d.getMonth().getValue();
        labels[2] = d.getDayOfMonth();
        labels[3] = d.getHour();
        labels[4] = d.getMinute();
//        for(int i = 0; i < 5; i++)
//            System.out.println(labels[i]);
        return labels;
    }


    public List<TreeNode> getQueryNodes(TreeNode node,
                                        List<TreeNode> queryNodes, int[] startLabels,
                                        int[] endLabels, boolean isFirst, boolean isLast, int level) {
        // we are at a leaf node
        Collection<TreeNode> children = node.getChildren();
        if (children == null || children.isEmpty()) {
            queryNodes.add(node);
            return queryNodes;
        }

        // these are the children's filters
        int start = startLabels[level];
        int end = endLabels[level];

        /* We filter in each level only in the first node and the last. If we are on the first node, we get everything that is from the start filter
        * and after. Else if we are in the last node we get everything before the end filter. Finally, if we re in intermediary nodes we get all children
        * that are below the filtered values of the current node.*/
        if(isFirst)
            children = children.stream().filter(child -> child.getLabel() >= start).collect(Collectors.toList());
        if(isLast)
            children = children.stream().filter(child -> child.getLabel() <= end).collect(Collectors.toList());

        for (TreeNode child : children) {
            // The child's first node will be the first node of the current first node and the same for the end
            boolean childIsFirst = child.getLabel() == start && isFirst;
            boolean childIsLast = child.getLabel() == end && isLast;
            queryNodes = getQueryNodes(child, queryNodes, startLabels, endLabels, childIsFirst, childIsLast, level + 1);
        }
        return queryNodes;
    }
}
