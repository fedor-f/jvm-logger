package ru.hse.cli;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

import picocli.CommandLine;
import ru.hse.api.CommandExecutor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

@Command(name = "jvm-logger", description = "collects JVM events", version = "0.0.1", mixinStandardHelpOptions = true)
public class JVMLoggerApplication implements Callable<Integer> {

    private static final String EVENT_DOC_LINK = "https://github.com/fedor-f/jvm-logger/blob/main/docs/Event%20Documentation.pdf";
    private static final Logger LOGGER = Logger.getLogger(JVMLoggerApplication.class.getName());

    @Option(names = {"-i", "--input"}, description = "Input .jar file path")
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

    @Option(names = {"-gc", "--garbage-collector"}, description = "Garbage collector implementation. Could be -XX:+UseSerialGC, -XX:+UseParallelGC, -XX:+UseG1GC, -XX:+UseZGC. Default option is -XX:+UseG1GC", defaultValue = "-XX:+UseG1GC")
    String gc;

    public static void main(String[] args) {
        new CommandLine(new JVMLoggerApplication()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        checkInputParam();

        logInputParams();

        var argsJar = String.join(" ", args);

        if (!new File(input).isFile()) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Input file does not exist by the given path: " + input);
        }

        validateGCOption(gc);

        Optional<Map<String, Integer>> opt;
        try {
            opt = CommandExecutor.normalEventCollection(input, jfrOutput, recordingDuration, output, argsJar, gc, showStatistics, verbose);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Incorrect format of recording duration. Try 1s, 1m1s, 1h etc.");
        } catch (IOException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), e.getMessage());
        }

        opt.ifPresent(stringIntegerMap -> {
            System.out.println();
            System.out.println("STATISTICS");
            stringIntegerMap.forEach((key, value) -> System.out.println(key + "\t" + value));
        });
        return 0;
    }

    private void validateGCOption(String gc) {
        List<String> gcOptions = List.of("-XX:+UseSerialGC", "-XX:+UseParallelGC", "-XX:+UseG1GC", "-XX:+UseZGC");

        if (!gcOptions.contains(gc)) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Incorrect garbage collection option. It could be one the following statements: -XX:+UseSerialGC, -XX:+UseParallelGC, -XX:+UseG1GC, -XX:+UseZGC");
        }
    }

    private void logInputParams() {
        LOGGER.info("Input .jar file path is: " + input);
        LOGGER.info("JFR events output file path is: " + jfrOutput);
        LOGGER.info("Event recording duration: " + recordingDuration);
        LOGGER.info("File output path is: " + output);
        LOGGER.info("Is event statistic included: " + showStatistics);
        LOGGER.info("String arguments for .jar: " + Arrays.toString(args));
        LOGGER.info("Is verbose logging: " + verbose);
        LOGGER.info("GC Implementation: " + gc);
    }

    @Command(name = "get-event-docs", description = "Gets a link to Event documentation", mixinStandardHelpOptions = true)
    Integer getEventDocumentation() throws MalformedURLException {
        var docURL = new URL(EVENT_DOC_LINK);

        System.out.println("The event documentation is stored here:\n" +
                docURL);

        return 0;
    }

    @Command(name = "filter-by-categories", description = "Enable collection of JVM events filtered by categories of events", mixinStandardHelpOptions = true)
    Integer filterByCategories(@Parameters(description = "Names of categories of events separated by comma e.g Java Virtual Machine,Runtime")
                                           String... categories) {
        checkInputParam();

        if (!new File(input).isFile()) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Input file does not exist by the given path: " + input);
        }

        if (categories == null) {
            throw new CommandLine.ParameterException(new CommandLine(this),
                    "The parameters categories is empty. Example: Java Virtual Machine,Runtime");
        }

        logInputParams();

        var argsJar = String.join(" ", args);

        var categoryString = String.join(" ", categories);

        var categoryArray = categoryString.split(",");

        var strippedArray = Arrays.stream(categoryArray).map(String::strip).toList();

        LOGGER.info("Category array: " + strippedArray);

        validateGCOption(gc);

        Optional<Map<String, Integer>> opt;

        try {
            opt = CommandExecutor.filteredByCategoriesEventCollection(input, jfrOutput, recordingDuration, output, strippedArray, argsJar, gc, showStatistics, verbose);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Incorrect format of recording duration. Try 1s, 1m1s, 1h etc.");
        } catch (IOException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), e.getMessage());
        }

        opt.ifPresent(result -> {
            System.out.println();
            System.out.println("STATISTICS");
            result.forEach((key, value) -> System.out.println(key + "\t" + value));
        });

        return 0;
    }

    @Command(name = "filter-by-names", description = "Enable collection of JVM events filtered by names of event types", mixinStandardHelpOptions = true)
    Integer filterByNames(@Parameters(description = "Names of event types separated by comma e.g jdk.ModuleExport,jdk.SystemProcess")
                          String... names) {
        checkInputParam();

        if (!new File(input).isFile()) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Input file does not exist by the given path: " + input);
        }

        if (names == null) {
            throw new CommandLine.ParameterException(new CommandLine(this),
                    "The parameters names is empty. Example: jdk.ModuleExport,jdk.SystemProcess");
        }

        logInputParams();

        var argsJar = String.join(" ", args);

        var namesString = String.join(" ", names);

        var nameArray = namesString.split(",");

        var strippedArray = Arrays.stream(nameArray).map(String::strip).toList();

        LOGGER.info("Name array: " + strippedArray);

        validateGCOption(gc);

        Optional<Map<String, Integer>> opt;

        try {
            opt = CommandExecutor.filteredByNamesEventCollection(input, jfrOutput, recordingDuration, output, strippedArray, argsJar, gc, showStatistics, verbose);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Incorrect format of recording duration. Try 1s, 1m1s, 1h etc.");
        } catch (IOException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), e.getMessage());
        }

        opt.ifPresent(result -> {
            System.out.println();
            System.out.println("STATISTICS");
            result.forEach((key, value) -> System.out.println(key + "\t" + value));
        });

        return 0;
    }

    private void checkInputParam() {
        if (input == null) {
            throw new CommandLine.ParameterException(new CommandLine(this),
                    "The input parameter -i is required");
        }
    }
}
