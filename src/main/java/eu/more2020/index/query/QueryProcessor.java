package eu.more2020.index.query;

import com.google.common.math.StatsAccumulator;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import eu.more2020.index.DataPoint;
import eu.more2020.index.Schema;
import eu.more2020.index.Tree;
import eu.more2020.index.TreeNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class QueryProcessor {

    private Query query;
    private QueryResults queryResults;
    private Schema schema;
    private int freqLevel;
    private Stack<TreeNode> stack = new Stack<>();
    private FileInputStream fileInputStream;

    private static final Logger LOG = LogManager.getLogger(QueryProcessor.class);

    public QueryProcessor(Query query, Schema schema) {
        this.query = query;
        this.queryResults = new QueryResults(query);
        this.schema = schema;
        this.freqLevel = schema.getTemporaltHierarchy().indexOf(ChronoField.valueOf(query.getFrequency())) + 1;
        LOG.debug(freqLevel);
    }

    public QueryResults prepareQueryResults(TreeNode root) throws IOException {
        LocalDateTime start = query.getRange().getFrom();
        LocalDateTime end = query.getRange().getTo();
        List<Integer> startLabels = getLabels(start);
        List<Integer> endLabels = getLabels(end);

        fileInputStream = new FileInputStream(schema.getCsv());
        this.processQueryNodes(root, startLabels, endLabels, true, true, 0);
        fileInputStream.close();
        return queryResults;
    }

    public void processNode(TreeNode treeNode) throws IOException {
        if (treeNode.getLevel() == freqLevel) {
            queryResults.getDataPoints().add(new DataPoint(getCurrentNodeDateTime(), treeNode.getStats().get(query.getMeasure()).mean()));
        } else {
            fileInputStream.getChannel().position(treeNode.getFileOffsetStart());
            CsvParserSettings parserSettings = schema.createCsvParserSettings();
            CsvParser parser = new CsvParser(parserSettings);
            parser.beginParsing(fileInputStream, StandardCharsets.UTF_8);
            String[] row;
            LocalDateTime previousDate, currentDate = null;
            List<TemporalField> temporalHierarchy = schema.getTemporaltHierarchy();
            StatsAccumulator statsAccumulator = new StatsAccumulator();
            for (int i = 0; i < treeNode.getDataPointCount(); i++) {
                row = parser.parseNext();
                previousDate = currentDate;
                currentDate = Schema.parseStringToDate(row[schema.getTimeColumn()]).truncatedTo(temporalHierarchy.get(freqLevel - 1).getBaseUnit());
                if (!previousDate.equals(currentDate) && previousDate != null) {
                    queryResults.getDataPoints().add(new DataPoint(previousDate, statsAccumulator.mean()));
                    statsAccumulator = new StatsAccumulator();
                } else {
                    statsAccumulator.add(Double.parseDouble(row[query.getMeasure()]));
                }
            }
            parser.stopParsing();
        }
    }

    private void processQueryNodes(TreeNode node, List<Integer> startLabels,
                                   List<Integer> endLabels, boolean isFirst, boolean isLast, int level) throws IOException {
        stack.push(node);
        // we are at a leaf node
        Collection<TreeNode> children = node.getChildren();
        if (children == null || children.isEmpty()) {
            processNode(node);
            stack.pop();
            return;
        }

        // these are the children's filters
        LOG.debug(startLabels);
        int start = startLabels.get(level);
        int end = endLabels.get(level);

        /* We filter in each level only in the first node and the last. If we are on the first node, we get everything that is from the start filter
         * and after. Else if we are in the last node we get everything before the end filter. Finally, if we re in intermediary nodes we get all children
         * that are below the filtered values of the current node.*/
        if (isFirst)
            children = children.stream().filter(child -> child.getLabel() >= start).collect(Collectors.toList());
        if (isLast)
            children = children.stream().filter(child -> child.getLabel() <= end).collect(Collectors.toList());

        for (TreeNode child : children) {
            // The child's first node will be the first node of the current first node and the same for the end
            boolean childIsFirst = child.getLabel() == start && isFirst;
            boolean childIsLast = child.getLabel() == end && isLast;
            processQueryNodes(child, startLabels, endLabels, childIsFirst, childIsLast, level + 1);
        }


    }

    private LocalDateTime getCurrentNodeDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(0, 1, 1, 0, 0, 0, 0);
        for (int i = 1; i <= freqLevel; i++) {
            dateTime = dateTime.with(schema.getTemporaltHierarchy().get(i - 1), stack.get(i).getLabel());
        }
        return dateTime;
    }

    private List<Integer> getLabels(LocalDateTime date) {
        List<Integer> labels = new ArrayList<>();
        for (TemporalField temporalField : schema.getTemporaltHierarchy()) {
            labels.add(date.get(temporalField));
        }
        return labels;
    }

}