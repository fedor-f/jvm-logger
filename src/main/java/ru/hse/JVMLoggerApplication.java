package ru.hse;

import ru.hse.collector.JarRunner;
import ru.hse.eventProcessing.JFREventProcessor;

public class JVMLoggerApplication {
    public static void main(String[] args) throws InterruptedException {
        var runner = new JarRunner();

        System.out.println("Executing .jar");
        var thread = new Thread(() ->
                runner.run("/users/fedorfilippov/Desktop/Jigsaw.jar",
                        "/users/fedorfilippov/Desktop/flight.jfr",
                        "20s"
                )
        );
        thread.start();

        Thread.sleep(21000);

        System.out.println(".jfr file collected");

        System.out.println("Collected events: ");

        var jfrEventProcessor = new JFREventProcessor();

        jfrEventProcessor.processEventsFromFile("/users/fedorfilippov/Desktop/flight.jfr", "output.xes");
    }
}
