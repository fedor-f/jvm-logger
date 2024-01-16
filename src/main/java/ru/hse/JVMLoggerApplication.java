package ru.hse;

import jdk.jfr.ValueDescriptor;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import ru.hse.collector.JarRunner;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class JVMLoggerApplication {
    public static void main(String[] args) {
        var runner = new JarRunner();

        System.out.println("Executing .jar");

        runner.run("/users/fedorfilippov/Desktop/helloWorldEvents-1.0-SNAPSHOT-jar-with-dependencies.jar",
                "flight.jfr",
                "200s"
        );

        System.out.println(".jfr file collected");

        System.out.println("Collected events: ");
        try (RecordingFile recordingFile = new RecordingFile(Paths.get("flight.jfr"))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();

                System.out.println("Event: " + event.getEventType().getName());

                List<ValueDescriptor> fields = event.getFields();

                for (ValueDescriptor field : fields) {
                    Object value = event.getValue(field.getName());

                    System.out.println("  " + field.getName() + " (" + field.getTypeName() + "): " + value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
