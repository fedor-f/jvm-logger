package ru.hse.api;

import ru.hse.collector.JarRunner;
import ru.hse.eventProcessing.JFREventProcessor;
import ru.hse.util.DurationUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class CommandExecutor {

    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class.getName());

    /**
     * Collect JVM events into .xes file.
     * @param input Path to .jar file.
     * @param jfrOutput .jfr file output path.
     * @param recordingDuration Event recording duration.
     * @param xesOutput Output path to .xes file of collected events.
     * @param args String arguments for .jar file. An empty string can be passed if arguments are not used.
     * @param showStatistics Boolean flag for showing statistics after collecting events.
     * @param verbose Boolean flag to enable verbose logging.
     * @return Optional object that can contain a map of String and integers describing event collection statistics.
     * If statistics are not enabled empty optional is returned.
     */
    public static Optional<Map<String, Integer>> normalEventCollection(String input,
                                                                       String jfrOutput,
                                                                       String recordingDuration,
                                                                       String xesOutput,
                                                                       String args,
                                                                       String gc,
                                                                       boolean showStatistics,
                                                                       boolean verbose) {
        Optional<Map<String, Integer>> opt;
        var thread = getThreadResponsibleForJarRunning(input, jfrOutput, recordingDuration, args, gc);

        var jfrEventProcessor = new JFREventProcessor();

        try {
            opt = jfrEventProcessor.processEventsFromFile(jfrOutput, xesOutput, showStatistics, verbose);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();

        LOGGER.info("Events collected");
        return opt;
    }

    /**
     * Collect JVM events filtered by categories into .xes file.
     * @param input Path to .jar file.
     * @param jfrOutput .jfr file output path.
     * @param recordingDuration Event recording duration.
     * @param xesOutput Output path to .xes file of collected events.
     * @param categories List of categories to filter.
     * @param args String arguments for .jar file. An empty string can be passed if arguments are not used.
     * @param showStatistics Boolean flag for showing statistics after collecting events.
     * @param verbose Boolean flag to enable verbose logging.
     * @return Optional object that can contain a map of String and integers describing event collection statistics.
     * If statistics are not enabled empty optional is returned.
     */
    public static Optional<Map<String, Integer>> filteredByCategoriesEventCollection(String input,
                                                                                     String jfrOutput,
                                                                                     String recordingDuration,
                                                                                     String xesOutput,
                                                                                     List<String> categories,
                                                                                     String args,
                                                                                     String gc,
                                                                                     boolean showStatistics,
                                                                                     boolean verbose) {
        Optional<Map<String, Integer>> opt;

        var thread = getThreadResponsibleForJarRunning(input, jfrOutput, recordingDuration, args, gc);

        var jfrEventProcessor = new JFREventProcessor();

        try {
            opt = jfrEventProcessor.processEventsFromFileFilteredByCategories(jfrOutput, xesOutput, categories, showStatistics, verbose);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();

        LOGGER.info("Events collected");
        return opt;
    }

    /**
     * Collect JVM events filtered by event type names into .xes file.
     * @param input Path to .jar file.
     * @param jfrOutput .jfr file output path.
     * @param recordingDuration Event recording duration.
     * @param xesOutput Output path to .xes file of collected events.
     * @param names List of event type names to filter.
     * @param args String arguments for .jar file. An empty string can be passed if arguments are not used.
     * @param showStatistics Boolean flag for showing statistics after collecting events.
     * @param verbose Boolean flag to enable verbose logging.
     * @return Optional object that can contain a map of String and integers describing event collection statistics.
     * If statistics are not enabled empty optional is returned.
     */
    public static Optional<Map<String, Integer>> filteredByNamesEventCollection(String input,
                                                                                String jfrOutput,
                                                                                String recordingDuration,
                                                                                String xesOutput,
                                                                                List<String> names,
                                                                                String args,
                                                                                String gc,
                                                                                boolean showStatistics,
                                                                                boolean verbose) {
        Optional<Map<String, Integer>> opt;

        var thread = getThreadResponsibleForJarRunning(input, jfrOutput, recordingDuration, args, gc);

        var jfrEventProcessor = new JFREventProcessor();

        try {
            opt = jfrEventProcessor.processEventsFromFileFilteredByNames(jfrOutput, xesOutput, names, showStatistics, verbose);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();

        LOGGER.info("Events collected");
        return opt;
    }

    private static Thread getThreadResponsibleForJarRunning(String input,
                                                            String jfrOutput,
                                                            String recordingDuration,
                                                            String args,
                                                            String gc) {
        var runner = new JarRunner();

        LOGGER.info("Executing .jar");
        var thread = new Thread(() ->
                runner.run(input, jfrOutput, recordingDuration, args, gc)
        );
        thread.start();

        try {
            Thread.sleep(DurationUtil.parseDuration(recordingDuration) * 1000 + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info(".jfr file collected");

        return thread;
    }
}
