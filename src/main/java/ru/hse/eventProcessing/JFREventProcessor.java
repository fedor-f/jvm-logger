package ru.hse.eventProcessing;

import ext.org.deckfour.xes.model.XEvent;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import ru.hse.XESprocessor.XESSerializerWrapper;
import ru.hse.config.EventDictionaryReader;
import ru.hse.config.entity.EventDescription;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JFREventProcessor {
    public void processEventsFromFile(String filePath, String outputXesFilePath) throws IOException {
        var serializer = new XESSerializerWrapper();
        var converter = new EventConverter();

        var configReader = new EventDictionaryReader();
        Map<String, Boolean> eventDescriptions = configReader.readEventDictionary();

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

                if (eventDescriptions.containsKey(event.getEventType().getName())) {
                    XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                    serializer.addEventToTrace(convertedEvent);
                }
            }

            serializer.serializeLog(outputXesFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
