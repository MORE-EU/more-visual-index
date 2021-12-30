package eu.more2020.index.experiments;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import eu.more2020.index.Range;
import eu.more2020.index.Schema;
import eu.more2020.index.Tree;
import eu.more2020.index.experiments.util.RangeConverter;
import eu.more2020.index.query.Query;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import static eu.more2020.index.config.IndexConfig.DELIMITER;

public class Experiments {
    @Parameter(names = "-csv", description = "The csv file")
    public String csv;
    @Parameter(names = "-timeCol", description = "The time column")
    private String timeCol;
    @Parameter(names = "-range", converter = RangeConverter.class, description = "Range")
    private Range range = null;
    @Parameter(names = "-freq", description = "Frequency")
    private Integer frequency;
    @Parameter(names = "--help", help = true, description = "Displays help")
    private boolean help;

    @Parameter(names = "-out", description = "The output file")
    private String outFile;

    public static void main(String... args) throws IOException, ClassNotFoundException {
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
        Schema schema = new Schema(csv, DELIMITER, Integer.parseInt(timeCol));

        Tree tree = new Tree(schema);
        Query q0 = new Query(range, frequency);
        tree.initialize(q0);
    }

}
