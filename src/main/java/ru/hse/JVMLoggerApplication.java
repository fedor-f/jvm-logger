package ru.hse;

import ru.hse.collector.JarRunner;
import ru.hse.eventProcessing.JFREventProcessor;

import java.io.IOException;

public class JVMLoggerApplication {
    public static void main(String[] args) throws InterruptedException, IOException {
        var runner = new JarRunner();

        System.out.println("Executing .jar");
        var thread = new Thread(() ->
                runner.run("/users/fedorfilippov/Desktop/Server.jar",
                        "/users/fedorfilippov/Desktop/flight.jfr",
                        "3s"
                )
        );
        thread.start();

        Thread.sleep(4000);

        System.out.println(".jfr file collected");

        System.out.println("Collected events: ");

        var jfrEventProcessor = new JFREventProcessor();

        jfrEventProcessor.processEventsFromFile("/users/fedorfilippov/Desktop/flight.jfr", "output.xes");
    }
}
