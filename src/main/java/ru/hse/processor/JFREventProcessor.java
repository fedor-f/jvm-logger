package ru.hse.processor;

import jdk.jfr.ValueDescriptor;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class JFREventProcessor {
    public void processEventsFromFile(String filePath) {
        try (RecordingFile recordingFile = new RecordingFile(Paths.get(filePath))) {
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
