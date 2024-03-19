package ru.hse.ui;

import static picocli.CommandLine.Command;

import picocli.CommandLine;
import ru.hse.collector.JarRunner;
import ru.hse.eventProcessing.JFREventProcessor;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "jvm-logger", description = "collects JVM events", version = "0.0.1")
public class JVMLoggerApplication implements Callable<Integer> {
    public static void main(String[] args) {
        new CommandLine(new JVMLoggerApplication()).execute();
    }

    @Override
    public Integer call() throws Exception {
        var runner = new JarRunner();

        System.out.println("Executing .jar");
        var thread = new Thread(() ->
                runner.run("/users/fedorfilippov/Desktop/Server.jar",
                        "/users/fedorfilippov/Desktop/flight.jfr",
                        "3s"
                )
        );
        thread.start();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(".jfr file collected");

        System.out.println("Collected events: ");

        var jfrEventProcessor = new JFREventProcessor();

        try {
            jfrEventProcessor.processEventsFromFile("/users/fedorfilippov/Desktop/flight.jfr", "output.xes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        thread.interrupt();
        return 0;
    }
}
