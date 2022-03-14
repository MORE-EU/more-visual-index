package eu.more2020.index;

import com.google.common.math.StatsAccumulator;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import eu.more2020.index.query.Query;
import eu.more2020.index.query.QueryProcessor;
import eu.more2020.index.query.QueryResults;
import eu.more2020.index.util.io.RandomAccessReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.*;

public class Tree {

    private static final Logger LOG = LogManager.getLogger(Tree.class);


    protected TreeNode root;

    private Schema schema;

    private int objectsIndexed = 0;

    private RandomAccessReader randomAccessReader;

    private boolean isInitialized = false;

    public Tree(Schema schema) {
        this.schema = schema;
    }

    private TreeNode addPoint(Stack<Integer> labels, long fileOffset, String[] row) {
        if (root == null) {
            root = new TreeNode(0, 0);
        }

        TreeNode node = root;
        if (node.getDataPointCount() == 0) {
            node.setFileOffsetStart(fileOffset);
        }
        node.setDataPointCount(node.getDataPointCount() + 1);
        node.adjustStats(row, schema);

        for (Integer label : labels) {
            TreeNode child = node.getOrAddChild(label);
            node = child;
            if (node.getDataPointCount() == 0) {
                node.setFileOffsetStart(fileOffset);
            }
            node.setDataPointCount(node.getDataPointCount() + 1);
            node.adjustStats(row, schema);
        }
        return node;
    }

    public QueryResults initialize(Query q0) {
        CsvParserSettings parserSettings = schema.createCsvParserSettings();
        // to be able to get file offset of first measurement
        parserSettings.setHeaderExtractionEnabled(false);
        CsvParser parser = new CsvParser(parserSettings);
        objectsIndexed = 0;

        parser.beginParsing(new File(schema.getCsv()), Charset.forName("US-ASCII"));
        long rowOffset = 0l;
        if (schema.getHasHeader()) {
            parser.parseNext();  //skip header row
            rowOffset = parser.getContext().currentChar() - 1;
        }

        String[] row;
        List<TemporalField> temporalHierarchy = schema.getTemporaltHierarchy();
        while ((row = parser.parseNext()) != null) {
            LocalDateTime dateTime = Schema.parseStringToDate(row[schema.getTimeColumn()]).truncatedTo(temporalHierarchy.get(temporalHierarchy.size() - 1).getBaseUnit());
            Stack<Integer> labels = new Stack<>();
            for (TemporalField temporalField : temporalHierarchy) {
                labels.add(dateTime.get(temporalField));
            }
            this.addPoint(labels, rowOffset, row);
            objectsIndexed++;
            rowOffset = parser.getContext().currentChar() - 1;

        }
        parser.stopParsing();
        isInitialized = true;
        LOG.debug("Indexing Complete. Total Indexed Objects: " + objectsIndexed);
        QueryResults queryResults = new QueryResults(q0);
        return queryResults;
    }

    public synchronized QueryResults executeQuery(Query query) throws IOException {
        if (!isInitialized) {
            return initialize(query);
        }
        QueryProcessor queryProcessor = new QueryProcessor(query, schema);
        return queryProcessor.prepareQueryResults(root);
    }

    public Map<Integer, StatsAccumulator> getStats(List<TreeNode> queryNodes) {
        Map<Integer, StatsAccumulator> stats = new HashMap<>();
        for (TreeNode node : queryNodes) {
            node.getStats().forEach(
                    (key, value) ->
                            stats.merge(key, value, (v1, v2) -> {
                                v1.addAll(v2);
                                return v1;
                            }));
        }
        return stats;
    }

    public void traverse(TreeNode node) {
        LOG.debug(node);
        Collection<TreeNode> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            for (TreeNode child : children) {
                traverse(child);
            }
        }
    }

//    public int getLevel(TreeNode node) {
//        int level = 0;
//        if (node != root)
//            do {
//                level++;
//                node = node.getParent();
//            } while (node != root);
//        return level;
//    }


}
