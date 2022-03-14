package eu.more2020.index.experiments;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Stopwatch;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import eu.more2020.index.Range;
import eu.more2020.index.Schema;
import eu.more2020.index.Tree;
import eu.more2020.index.experiments.util.DateConverter;
import eu.more2020.index.query.Query;
import eu.more2020.index.query.QueryResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static eu.more2020.index.config.IndexConfig.DELIMITER;

public class Experiments {

    private static final Logger LOG = LogManager.getLogger(Experiments.class);

    @Parameter(names = "-csv", description = "The csv file")
    public String csv;
    @Parameter(names = "-timeCol", description = "The time column")
    private String timeCol;
    @Parameter(names = "-from", converter = DateConverter.class, description = "Range")
    private LocalDateTime from;
    @Parameter(names = "-to", converter = DateConverter.class, description = "Range")
    private LocalDateTime to;
    @Parameter(names = "-freq", description = "Frequency")
    private String frequency;
    @Parameter(names = "-out", description = "The output file")
    private String outFile;

    @Parameter(names = "--help", help = true, description = "Displays help")
    private boolean help;

    public static void main(String... args) throws IOException {
        Experiments experiments = new Experiments();
        JCommander jCommander = new JCommander(experiments, args);
        if (experiments.help) {
            jCommander.usage();
        } else {
            experiments.run();
        }
    }

    private void run() throws IOException {
        CsvWriterSettings csvWriterSettings = new CsvWriterSettings();
        boolean addHeader = new File(outFile).length() == 0;
        CsvWriter csvWriter = new CsvWriter(new FileWriter(outFile, true), csvWriterSettings);
        Schema schema = new Schema(csv, DELIMITER, Integer.parseInt(timeCol), true);

        Tree tree = new Tree(schema);

        Query q0 = new Query(new Range(from, to), 1, frequency);
        LOG.debug(q0);
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        stopwatch.start();
        tree.initialize(q0);
        stopwatch.stop();
        LOG.debug("Indexing Complete: " + stopwatch.elapsed().toMillis() / 1000 + " sec.");
        stopwatch.reset();

        stopwatch.start();
        QueryResults queryResults = tree.executeQuery(q0);
        stopwatch.stop();
        LOG.debug("Query Complete: " + stopwatch.elapsed().toMillis() + " ms");
        LOG.debug(queryResults);

    }

}
