package ru.hse.eventProcessing;

import ext.org.deckfour.xes.model.XEvent;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import ru.hse.XESprocessor.XESSerializerWrapper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class JFREventProcessor {

    private static final String DURATION_EVENT = "Record event with duration above or equal to threshold";

    private long eventNumber = 0;

    public Optional<Map<String, Integer>> processEventsFromFile(String filePath, String outputXesFilePath, boolean showStatistics) throws IOException {
        var serializer = new XESSerializerWrapper();
        var converter = new JFRToXESEventConverter();
        Optional<Map<String, Integer>> result;

        List<RecordedEvent> events = new ArrayList<>();

        try (RecordingFile recordingFile = new RecordingFile(Paths.get(filePath))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();

                events.add(event);
            }

            events.sort(Comparator.comparing(RecordedEvent::getStartTime));

            result = saveToXESFormat(outputXesFilePath, events, converter, serializer, showStatistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private Optional<Map<String, Integer>> saveToXESFormat(String outputXesFilePath, List<RecordedEvent> events,
                                                           JFRToXESEventConverter converter, XESSerializerWrapper serializer, boolean showStatistics) throws IOException {
        Map<String, Integer> eventMap = new HashMap<>();

        events.forEach(event -> {
            for (var settingDescriptor : event.getEventType().getSettingDescriptors()) {
                if (settingDescriptor.getDescription().equals(DURATION_EVENT)) {
                    eventNumber++;
                    logAllCollectedEventsFromJFRFile(event);
                    getStatistics(event, eventMap);

                    XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                    serializer.addEventToTrace(convertedEvent);

                    break;
                }
            }
        });

        serializer.serializeLog(outputXesFilePath);

        System.out.println("EVENT NUMBER = " + eventNumber);

        if (showStatistics) {
            return Optional.of(eventMap);
        }
        return Optional.empty();
    }

    private void getStatistics(RecordedEvent event, Map<String, Integer> eventMap) {
        var eventKey = event.getEventType().getName();

        if (!eventMap.containsKey(eventKey)) {
            eventMap.put(eventKey, 1);
        } else {
            eventMap.put(eventKey, eventMap.get(eventKey) + 1);
        }
    }

    public Optional<Map<String, Integer>> processEventsFromFileFilteredByCategories(String filePath,
                                                                                    String outputXesFilePath,
                                                                                    List<String> categories,
                                                                                    boolean showStatistics) throws IOException {
        var serializer = new XESSerializerWrapper();
        var converter = new JFRToXESEventConverter();
        Optional<Map<String, Integer>> opt;

        List<RecordedEvent> events = new ArrayList<>();

        try (RecordingFile recordingFile = new RecordingFile(Paths.get(filePath))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                events.add(event);
            }

            events.sort(Comparator.comparing(RecordedEvent::getStartTime));

            System.out.println("EVENT NUMBER = " + eventNumber);
            opt = saveToXESFilteredByCategories(outputXesFilePath, categories, events, converter, serializer, showStatistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return opt;
    }

    private Optional<Map<String, Integer>> saveToXESFilteredByCategories(String outputXesFilePath,
                                                                         List<String> categories,
                                                                         List<RecordedEvent> events,
                                                                         JFRToXESEventConverter converter,
                                                                         XESSerializerWrapper serializer,
                                                                         boolean showStatistics) throws IOException {
        Map<String, Integer> eventMap = new HashMap<>();

        events.forEach(event -> {
            for (var settingDescriptor : event.getEventType().getSettingDescriptors()) {
                if (settingDescriptor.getDescription().equals(DURATION_EVENT) &&
                        new HashSet<>(event.getEventType().getCategoryNames())
                                .stream().anyMatch(categories::contains)) {
                    eventNumber++;
                    logAllCollectedEventsFromJFRFile(event);
                    getStatistics(event, eventMap);

                    XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                    serializer.addEventToTrace(convertedEvent);

                    break;
                }
            }
        });

        serializer.serializeLog(outputXesFilePath);

        if (showStatistics) {
            return Optional.of(eventMap);
        }
        return Optional.empty();
    }

    public Optional<Map<String, Integer>> processEventsFromFileFilteredByNames(String filePath,
                                                                               String outputXesFilePath,
                                                                               List<String> names,
                                                                               boolean showStatistics) throws IOException {
        var serializer = new XESSerializerWrapper();
        var converter = new JFRToXESEventConverter();
        Optional<Map<String, Integer>> opt;

        List<RecordedEvent> events = new ArrayList<>();

        try (RecordingFile recordingFile = new RecordingFile(Paths.get(filePath))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                events.add(event);
            }

            events.sort(Comparator.comparing(RecordedEvent::getStartTime));

            System.out.println("EVENT NUMBER = " + eventNumber);
            opt = saveToXESFilteredByNames(outputXesFilePath, names, events, converter, serializer, showStatistics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return opt;
    }

    private Optional<Map<String, Integer>> saveToXESFilteredByNames(String outputXesFilePath, List<String> names,
                                                                    List<RecordedEvent> events, JFRToXESEventConverter converter,
                                                                    XESSerializerWrapper serializer,
                                                                    boolean showStatistics) throws IOException {
        Map<String, Integer> eventMap = new HashMap<>();


        events.forEach(event -> {
            for (var settingDescriptor : event.getEventType().getSettingDescriptors()) {
                if (settingDescriptor.getDescription().equals(DURATION_EVENT) &&
                        names.contains(event.getEventType().getName())) {
                    eventNumber++;
                    logAllCollectedEventsFromJFRFile(event);
                    getStatistics(event, eventMap);

                    XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                    serializer.addEventToTrace(convertedEvent);

                    break;
                }
            }
        });

        serializer.serializeLog(outputXesFilePath);

        if (showStatistics) {
            return Optional.of(eventMap);
        }
        return Optional.empty();
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
