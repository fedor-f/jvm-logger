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

    @Option(names = {"-o", "--output"}, description = "File output path", defaultValue = "./output.xes")
    String output;

    @Option(names = {"-s", "--stat"}, description = "Display statistics of collected events")
    boolean showStatistics;

    @Option(names = {"-a", "--args"}, arity = "1..*", description = "String arguments for .jar", defaultValue = "")
    String[] args;

    public static void main(String[] args) {
        new CommandLine(new JVMLoggerApplication()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        var argsJar = String.join(" ", args);

        var opt = CommandExecutor.normalEventCollection(input, jfrOutput, recordingDuration, output, argsJar, showStatistics);

        opt.ifPresent(stringIntegerMap -> {
            System.out.println("STATISTICS");
            stringIntegerMap.forEach((key, value) -> System.out.println(key + "\t" + value));
        });
        return 0;
    }

    @Command(name = "filter-by-categories", description = "Enable collection of JVM events filtered by categories of events", mixinStandardHelpOptions = true)
    Integer filterByCategories(@Parameters(description = "Names of categories of events separated by comma e.g Java Virtual Machine,Runtime")
                                           String... categories) {
        var argsJar = String.join(" ", args);

        var categoryString = String.join(" ", categories);

        var categoryArray = categoryString.split(",");

        var strippedArray = Arrays.stream(categoryArray).map(String::strip).toList();

        var opt = CommandExecutor.filteredByCategoriesEventCollection(input, jfrOutput, recordingDuration, output, strippedArray, argsJar, showStatistics);

        opt.ifPresent(result -> {
            System.out.println("STATISTICS");
            result.forEach((key, value) -> System.out.println(key + "\t" + value));
        });

        return 0;
    }

    // TODO: add subcommand order
    @Command(name = "filter-by-names", description = "Enable collection of JVM events filtered by names of event types", mixinStandardHelpOptions = true)
    Integer filterByNames(@Parameters(description = "Names of event types separated by comma e.g jdk.ModuleExport,jdk.SystemProcess")
                          String... names) {
        var argsJar = String.join(" ", args);

        var namesString = String.join(" ", names);

        var nameArray = namesString.split(",");

        var strippedArray = Arrays.stream(nameArray).map(String::strip).toList();

        var opt = CommandExecutor.filteredByNamesEventCollection(input, jfrOutput, recordingDuration, output, strippedArray, argsJar, showStatistics);

        opt.ifPresent(result -> {
            System.out.println("STATISTICS");
            result.forEach((key, value) -> System.out.println(key + "\t" + value));
        });

        return 0;
    }
}
