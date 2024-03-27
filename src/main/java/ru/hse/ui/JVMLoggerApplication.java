package ru.hse.ui;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

import picocli.CommandLine;
import ru.hse.core.CommandExecutor;

import java.util.Arrays;
import java.util.concurrent.Callable;

@Command(name = "jvm-logger", description = "collects JVM events", version = "0.0.1", mixinStandardHelpOptions = true)
public class JVMLoggerApplication implements Callable<Integer> {

    @Option(names = {"-i", "--input"}, description = "Input .jar file path", required = true)
    String input;

    @Option(names = {"-jfr", "--jfr-output"}, description = "JFR events output file path", defaultValue = "./flight.jfr")
    String jfrOutput;

    @Option(names = {"-d", "--duration"}, description = "Event recording duration", defaultValue = "2s")
    String recordingDuration;

    @Option(names = {"-f", "--format"}, description = "Output file format. Can be either .xes or .csv. Default: .xes", defaultValue = ".xes")
    String format;

    @Option(names = {"-o", "--output"}, description = "File output path", defaultValue = "./output.xes")
    String output;

    public static void main(String[] args) {
        new CommandLine(new JVMLoggerApplication()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        CommandExecutor.normalEventCollection(input, jfrOutput, recordingDuration, output);
        return 0;
    }

    @Command(name = "filter-by-categories", description = "Enable collection of JVM events filtered by categories of events", mixinStandardHelpOptions = true)
    Integer filterByCategories(@Parameters(description = "Names of categories of events separated by comma e.g Java Virtual Machine,Runtime")
                                           String... categories) {
        System.out.println("filterByCategories");
        var categoryString = String.join(" ", categories);

        var categoryArray = categoryString.split(",");

        var strippedArray = Arrays.stream(categoryArray).map(String::strip).toList();

        CommandExecutor.filteredByCategoriesEventCollection(input, jfrOutput, recordingDuration, output, strippedArray);
        return 0;
    }

    // TODO: add subcommand order
    @Command(name = "filter-by-names", description = "Enable collection of JVM events filtered by names of event types", mixinStandardHelpOptions = true)
    Integer filterByNames(@Parameters(description = "Names of event types separated by comma e.g jdk.ModuleExport,jdk.SystemProcess")
                          String... names) {
        System.out.println("filterByNames");

        var namesString = String.join(" ", names);

        var nameArray = namesString.split(",");

        var strippedArray = Arrays.stream(nameArray).map(String::strip).toList();

        CommandExecutor.filteredByNamesEventCollection(input, jfrOutput, recordingDuration, output, strippedArray);

        return 0;
    }
}
