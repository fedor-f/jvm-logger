package ru.hse.eventProcessing;

import ext.org.deckfour.xes.model.XEvent;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import ru.hse.XESprocessor.XESSerializerWrapper;
import ru.hse.config.EventDictionaryReader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class JFREventProcessor {

    private static final String DURATION_EVENT = "Record event with duration above or equal to threshold";

    private long eventNumber = 0;

    public void processEventsFromFile(String filePath, String outputXesFilePath) throws IOException {
        var serializer = new XESSerializerWrapper();
        var converter = new JFRToXESEventConverter();

        List<RecordedEvent> events = new ArrayList<>();

        try (RecordingFile recordingFile = new RecordingFile(Paths.get(filePath))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();

                events.add(event);
            }

            events.sort(Comparator.comparing(RecordedEvent::getStartTime));

            // TODO: add switch statement whether to save in .xes or .csv
            saveToXESFormat(outputXesFilePath, events, converter, serializer);

            System.out.println("EVENT NUMBER = " + eventNumber);
            eventNumber = 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToXESFormat(String outputXesFilePath, List<RecordedEvent> events, JFRToXESEventConverter converter, XESSerializerWrapper serializer) throws IOException {
        events.forEach(event -> {
            event.getEventType().getSettingDescriptors().forEach(settingDescriptor -> {
                if (settingDescriptor.getDescription().equals(DURATION_EVENT)) {
                    eventNumber++;
                    logAllCollectedEventsFromJFRFile(event);

                    XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                    serializer.addEventToTrace(convertedEvent);
                }
            });
        });

        serializer.serializeLog(outputXesFilePath);
    }

    public void processEventsFromFileFilteredByCategories(String filePath, String outputXesFilePath, List<String> categories) throws IOException {
        var serializer = new XESSerializerWrapper();
        var converter = new JFRToXESEventConverter();

        var configReader = new EventDictionaryReader();
        Map<String, Boolean> eventDescriptions = configReader.readEventDictionary();
        List<RecordedEvent> events = new ArrayList<>();

        try (RecordingFile recordingFile = new RecordingFile(Paths.get(filePath))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                events.add(event);
            }

            events.sort(Comparator.comparing(RecordedEvent::getStartTime));

            events.forEach(event -> {
                if (eventDescriptions.containsKey(event.getEventType().getName())) {
                    if (new HashSet<>(event.getEventType().getCategoryNames())
                            .stream().anyMatch(categories::contains)) {
                        logAllCollectedEventsFromJFRFile(event);

                        XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                        serializer.addEventToTrace(convertedEvent);
                    }
                }
            });

            serializer.serializeLog(outputXesFilePath);

            System.out.println("EVENT NUMBER = " + eventNumber);
            eventNumber = 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processEventsFromFileFilteredByNames(String filePath, String outputXesFilePath, List<String> names) throws IOException {
        var serializer = new XESSerializerWrapper();
        var converter = new JFRToXESEventConverter();

        var configReader = new EventDictionaryReader();
        Map<String, Boolean> eventDescriptions = configReader.readEventDictionary();
        List<RecordedEvent> events = new ArrayList<>();

        try (RecordingFile recordingFile = new RecordingFile(Paths.get(filePath))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                events.add(event);
            }

            events.sort(Comparator.comparing(RecordedEvent::getStartTime));

            events.forEach(event -> {
                if (eventDescriptions.containsKey(event.getEventType().getName())) {
                    if (names.contains(event.getEventType().getName())) {
                        logAllCollectedEventsFromJFRFile(event);

                        XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                        serializer.addEventToTrace(convertedEvent);
                    }
                }
            });

            serializer.serializeLog(outputXesFilePath);

            System.out.println("EVENT NUMBER = " + eventNumber);
            eventNumber = 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logAllCollectedEventsFromJFRFile(RecordedEvent event) {
        System.out.println("Categories " + event.getEventType().getCategoryNames());
        System.out.println("Event: " + event.getEventType().getName());

        List<ValueDescriptor> fields = event.getFields();

        for (ValueDescriptor field : fields) {
            Object value = event.getValue(field.getName());

            System.out.println("  " + field.getName() + " (" + field.getTypeName() + "): " + value);
        }
    }
}
