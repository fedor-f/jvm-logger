package ru.hse.core;

import ru.hse.collector.JarRunner;
import ru.hse.eventProcessing.JFREventProcessor;
import ru.hse.util.DurationUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandExecutor {
    public static Optional<Map<String, Integer>> normalEventCollection(String input, String jfrOutput,
                                                                       String recordingDuration, String xesOutput,
                                                                       boolean showStatistics) {
        Optional<Map<String, Integer>> opt;
        var thread = getThreadResponsibleForJarRunning(input, jfrOutput, recordingDuration);

        var jfrEventProcessor = new JFREventProcessor();

        try {
            opt = jfrEventProcessor.processEventsFromFile(jfrOutput, xesOutput, showStatistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();

        return opt;
    }

    public static Optional<Map<String, Integer>> filteredByCategoriesEventCollection(String input, String jfrOutput,
                                                           String recordingDuration, String xesOutput, List<String> categories,
                                                                                     boolean showStatistics) {
        Optional<Map<String, Integer>> opt;

        var thread = getThreadResponsibleForJarRunning(input, jfrOutput, recordingDuration);

        var jfrEventProcessor = new JFREventProcessor();

        try {
            opt = jfrEventProcessor.processEventsFromFileFilteredByCategories(jfrOutput, xesOutput, categories, showStatistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();

        return opt;
    }

    public static Optional<Map<String, Integer>> filteredByNamesEventCollection(String input, String jfrOutput,
                                                           String recordingDuration, String xesOutput, List<String> names,
                                                                                boolean showStatistics) {
        Optional<Map<String, Integer>> opt;

        var thread = getThreadResponsibleForJarRunning(input, jfrOutput, recordingDuration);

        var jfrEventProcessor = new JFREventProcessor();

        try {
            opt = jfrEventProcessor.processEventsFromFileFilteredByNames(jfrOutput, xesOutput, names, showStatistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();

        return opt;
    }

    private static Thread getThreadResponsibleForJarRunning(String input, String jfrOutput, String recordingDuration) {
        var runner = new JarRunner();

        System.out.println("Executing .jar");
        var thread = new Thread(() ->
                runner.run(input, jfrOutput, recordingDuration)
        );
        thread.start();

        try {
            Thread.sleep(DurationUtil.parseDuration(recordingDuration) * 1000 + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(".jfr file collected");

        System.out.println("Collected events: ");
        return thread;
    }
}
