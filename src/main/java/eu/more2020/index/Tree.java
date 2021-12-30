package eu.more2020.index;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import eu.more2020.index.experiments.util.QueryUtils;
import eu.more2020.index.query.Query;

import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class Tree {

    protected TreeNode root;

    private Schema schema;

    private int objectsIndexed = 0;

    private boolean isInitialized = false;
    public Tree(Schema schema) {
        this.schema = schema;
    }

    protected TreeNode addPoint(Point point, Stack<Integer> labels) {
        return getOrAddCategoricalNode(labels).addPoint(point);
    }

    private TreeNode getOrAddCategoricalNode(Stack<Integer> labels) {
        if (root == null) {
            root = new TreeNode((Integer) 0);
        }
        TreeNode node = root;
        for (Integer label : labels) {
            TreeNode child = node.getOrAddChild(label);
            node = child;
        }
        return node;
    }

    public void initialize(Query q0) {
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
                e.printStackTrace();
                //LOG.error("Problem parsing row number " + objectsIndexed + ": " + Arrays.toString(row), e);
                continue;
            } finally {
                rowOffset = parser.getContext().currentChar() - 1;
            }

        }
        System.out.println(root.getChildren());
        //System.out.println(root.getChild(2018).getChild(1).getChild(3).getChild(0).getStats().get(0).snapshot());
        parser.stopParsing();
        isInitialized = true;
        System.out.println("Indexing Complete. Total Indexed Objects:" + objectsIndexed);
//        LOG.debug("Indexing Complete. Total Indexed Objects: " + objectsIndexed);
//        QueryResults queryResults = new QueryResults(q0);
//        return queryResults;
    }
}
