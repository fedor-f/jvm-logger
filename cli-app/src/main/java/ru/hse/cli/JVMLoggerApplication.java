package ru.hse.cli;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

import picocli.CommandLine;
import ru.hse.api.CommandExecutor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

@Command(name = "jvm-logger", description = "collects JVM events", version = "0.0.1", mixinStandardHelpOptions = true)
public class JVMLoggerApplication implements Callable<Integer> {

    private static final Logger LOGGER = Logger.getLogger(JVMLoggerApplication.class.getName());

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

    @Option(names = {"-v", "--verbose"}, description = "Log events verbose?")
    boolean verbose;

    // TODO: extend if possible
    @Option(names = "--jfr-settings", description = "Path to .jfc file with Java Flight Recorder Settings", defaultValue = "")
    String jfrSettings;

    public static void main(String[] args) {
        new CommandLine(new JVMLoggerApplication()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        logInputParams();

        var argsJar = String.join(" ", args);

        var opt = CommandExecutor.normalEventCollection(input, jfrOutput, recordingDuration, output, argsJar, jfrSettings, showStatistics, verbose);

        opt.ifPresent(stringIntegerMap -> {
            System.out.println();
            System.out.println("STATISTICS");
            stringIntegerMap.forEach((key, value) -> System.out.println(key + "\t" + value));
        });
        return 0;
    }

    private void logInputParams() {
        LOGGER.info("Input .jar file path is: " + input);
        LOGGER.info("JFR events output file path is: " + jfrOutput);
        LOGGER.info("Event recording duration: " + recordingDuration);
        LOGGER.info("File output path is: " + output);
        LOGGER.info("Is event statistic included: " + showStatistics);
        LOGGER.info("String arguments for .jar: " + Arrays.toString(args));
        LOGGER.info("Is verbose logging: " + verbose);
        LOGGER.info("JFC settings file path: " + jfrSettings);
    }

    @Command(name = "get-event-docs", description = "Gets a link to Event documentation", mixinStandardHelpOptions = true)
    Integer getEventDocumentation() throws MalformedURLException {
        var docURL = new URL("https://github.com/fedor-f/jvm-logger/blob/main/docs/Event%20Documentation.pdf");

        System.out.println("The event documentation is stored here:\n" +
                docURL);

        return 0;
    }

    @Command(name = "filter-by-categories", description = "Enable collection of JVM events filtered by categories of events", mixinStandardHelpOptions = true)
    Integer filterByCategories(@Parameters(description = "Names of categories of events separated by comma e.g Java Virtual Machine,Runtime")
                                           String... categories) {
        logInputParams();

        var argsJar = String.join(" ", args);

        var categoryString = String.join(" ", categories);

        var categoryArray = categoryString.split(",");

        var strippedArray = Arrays.stream(categoryArray).map(String::strip).toList();

        LOGGER.info("Category array: " + strippedArray);

        var opt = CommandExecutor.filteredByCategoriesEventCollection(input, jfrOutput, recordingDuration, output, strippedArray, argsJar, jfrSettings, showStatistics, verbose);

        opt.ifPresent(result -> {
            System.out.println();
            System.out.println("STATISTICS");
            result.forEach((key, value) -> System.out.println(key + "\t" + value));
        });

        return 0;
    }

    // TODO: add subcommand order
    @Command(name = "filter-by-names", description = "Enable collection of JVM events filtered by names of event types", mixinStandardHelpOptions = true)
    Integer filterByNames(@Parameters(description = "Names of event types separated by comma e.g jdk.ModuleExport,jdk.SystemProcess")
                          String... names) {
        logInputParams();
        var argsJar = String.join(" ", args);

        var namesString = String.join(" ", names);

        var nameArray = namesString.split(",");

        var strippedArray = Arrays.stream(nameArray).map(String::strip).toList();

        LOGGER.info("Name array: " + strippedArray);

        var opt = CommandExecutor.filteredByNamesEventCollection(input, jfrOutput, recordingDuration, output, strippedArray, argsJar, jfrSettings, showStatistics, verbose);

        opt.ifPresent(result -> {
            System.out.println();
            System.out.println("STATISTICS");
            result.forEach((key, value) -> System.out.println(key + "\t" + value));
        });

        return 0;
    }
}
