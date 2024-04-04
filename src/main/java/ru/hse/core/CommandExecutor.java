package ru.hse.core;

import ru.hse.collector.JarRunner;
import ru.hse.eventProcessing.JFREventProcessor;
import ru.hse.util.DurationUtil;

import java.io.IOException;
import java.util.List;

public class CommandExecutor {
    public static void normalEventCollection(String input, String jfrOutput,
                                             String recordingDuration, String xesOutput) {
        var thread = getThreadResponsibleForJarRunning(input, jfrOutput, recordingDuration);

        var jfrEventProcessor = new JFREventProcessor();

        try {
            jfrEventProcessor.processEventsFromFile(jfrOutput, xesOutput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();
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

    public static void filteredByCategoriesEventCollection(String input, String jfrOutput,
                                                           String recordingDuration, String xesOutput, List<String> categories) {
        var thread = getThreadResponsibleForJarRunning(input, jfrOutput, recordingDuration);

        var jfrEventProcessor = new JFREventProcessor();

        try {
            jfrEventProcessor.processEventsFromFileFilteredByCategories(jfrOutput, xesOutput, categories);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();
    }

    public static void filteredByNamesEventCollection(String input, String jfrOutput,
                                                           String recordingDuration, String xesOutput, List<String> names) {
        var thread = getThreadResponsibleForJarRunning(input, jfrOutput, recordingDuration);

        var jfrEventProcessor = new JFREventProcessor();

        try {
            jfrEventProcessor.processEventsFromFileFilteredByNames(jfrOutput, xesOutput, names);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();
    }
}
