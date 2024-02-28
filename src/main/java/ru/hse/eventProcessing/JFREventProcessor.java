package ru.hse.eventProcessing;

import ext.org.deckfour.xes.model.XEvent;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import ru.hse.XESprocessor.XESSerializerWrapper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class JFREventProcessor {
    public void processEventsFromFile(String filePath, String outputXesFilePath) {
        var serializer = new XESSerializerWrapper();
        var converter = new EventConverter();

        try (RecordingFile recordingFile = new RecordingFile(Paths.get(filePath))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();

//                System.out.println("Event: " + event.getEventType().getName());
//
//                List<ValueDescriptor> fields = event.getFields();
//
//                for (ValueDescriptor field : fields) {
//                    Object value = event.getValue(field.getName());
//
//                    System.out.println("  " + field.getName() + " (" + field.getTypeName() + "): " + value);
//                }

                XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                serializer.addEventToTrace(convertedEvent);
            }

            serializer.serializeLog(outputXesFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
