package ru.hse.ui;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

import picocli.CommandLine;
import ru.hse.collector.JarRunner;
import ru.hse.eventProcessing.JFREventProcessor;
import ru.hse.util.DurationUtil;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "jvm-logger", description = "collects JVM events", version = "0.0.1")
public class JVMLoggerApplication implements Callable<Integer> {

    @Option(names = {"-i", "--input"}, description = "Input .jar file path", required = true)
    String input;

    // not mandatory for user
    @Option(names = {"-jfr", "--jfr-output"}, description = "JFR events output file path", defaultValue = "./")
    String jfrOutput;

    // set default value
    @Option(names = {"-d", "--duration"}, description = "Event recording duration", defaultValue = "2s")
    String recordingDuration;

    @Option(names = {"-o", "--output"}, description = ".xes file output path", required = true)
    String xesOutput;

    public static void main(String[] args) {
        new CommandLine(new JVMLoggerApplication()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
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

        var jfrEventProcessor = new JFREventProcessor();

        try {
            jfrEventProcessor.processEventsFromFile(jfrOutput, xesOutput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();
        return 0;
    }
}
