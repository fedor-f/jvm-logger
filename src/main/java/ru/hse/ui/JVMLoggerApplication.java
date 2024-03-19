package ru.hse.ui;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

import picocli.CommandLine;
import ru.hse.core.CommandExecutor;

import java.util.concurrent.Callable;

@Command(name = "jvm-logger", description = "collects JVM events", version = "0.0.1", mixinStandardHelpOptions = true)
public class JVMLoggerApplication implements Callable<Integer> {

    @Option(names = {"-i", "--input"}, description = "Input .jar file path", required = true)
    String input;

    @Option(names = {"-jfr", "--jfr-output"}, description = "JFR events output file path", defaultValue = "./flight.jfr")
    String jfrOutput;

    @Option(names = {"-d", "--duration"}, description = "Event recording duration", defaultValue = "2s")
    String recordingDuration;

    @Option(names = {"-o", "--output"}, description = ".xes file output path", defaultValue = "./output.xes")
    String xesOutput;

    public static void main(String[] args) {
        new CommandLine(new JVMLoggerApplication()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        CommandExecutor.normalEventCollection(input, jfrOutput, recordingDuration, xesOutput);
        return 0;
    }

    // FIXME
    @Command(name = "filter-by-categories", description = "Enable collection of JVM events filtered by categories of events")
    Integer filterByCategories(@Parameters(description = "Names of categories of events separated by comma i.e Java Virtual Machine,GC")
                                           String categories) {
        var categoryArray = categories.split(",");
        CommandExecutor.filteredByCatogoriesEventCollection(input, jfrOutput, recordingDuration, xesOutput, categoryArray);
        return 0;
    }
}
