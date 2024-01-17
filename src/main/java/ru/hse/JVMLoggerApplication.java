package ru.hse;

import ext.org.deckfour.xes.factory.XFactoryBufferedImpl;
import ext.org.deckfour.xes.out.XesXmlSerializer;
import ru.hse.collector.JarRunner;
import ru.hse.processor.JFREventProcessor;


public class JVMLoggerApplication {
    public static void main(String[] args) throws InterruptedException {
        var runner = new JarRunner();

        System.out.println("Executing .jar");
        var thread = new Thread(() ->
                runner.run("/users/fedorfilippov/Desktop/Jigsaw.jar",
                        "/users/fedorfilippov/Desktop/flight.jfr",
                        "3s"
                )
        );
        thread.start();

        Thread.sleep(4000);

        System.out.println(".jfr file collected");

        System.out.println("Collected events: ");

        var jfrEventProcessor = new JFREventProcessor();

        jfrEventProcessor.processEventsFromFile("/users/fedorfilippov/Desktop/flight.jfr");
    }
}
