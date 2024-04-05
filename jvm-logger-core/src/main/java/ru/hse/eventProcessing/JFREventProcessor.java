package ru.hse.eventProcessing;

import ext.org.deckfour.xes.model.XEvent;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import ru.hse.XESprocessor.XESSerializerWrapper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class JFREventProcessor {

    private static final String DURATION_EVENT = "Record event with duration above or equal to threshold";

    private static final Logger LOGGER = Logger.getLogger(JFREventProcessor.class.getName());

    public Optional<Map<String, Integer>> processEventsFromFile(String filePath,
                                                                String outputXesFilePath,
                                                                boolean showStatistics,
                                                                boolean verbose) throws IOException {
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

            result = saveToXESFormat(outputXesFilePath, events, converter, serializer, showStatistics, verbose);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private Optional<Map<String, Integer>> saveToXESFormat(String outputXesFilePath,
                                                           List<RecordedEvent> events,
                                                           JFRToXESEventConverter converter,
                                                           XESSerializerWrapper serializer,
                                                           boolean showStatistics,
                                                           boolean verbose) throws IOException {
        Map<String, Integer> eventMap = new HashMap<>();
        var eventNumber = 0;

        for (var event : events) {
            for (var settingDescriptor : event.getEventType().getSettingDescriptors()) {
                if (settingDescriptor.getDescription().equals(DURATION_EVENT)) {
                    if (verbose) {
                        logAllCollectedEventsFromJFRFile(event);
                    }
                    getStatistics(event, eventMap);

                    XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                    serializer.addEventToTrace(convertedEvent);

                    eventNumber++;
                    break;
                }
            }
        }

        LOGGER.info("EVENT NUMBER = " + eventNumber);
        serializer.serializeLog(outputXesFilePath);

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
                                                                                    boolean showStatistics,
                                                                                    boolean verbose) throws IOException {
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

            opt = saveToXESFilteredByCategories(outputXesFilePath, categories, events, converter, serializer, showStatistics, verbose);
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
                                                                         boolean showStatistics,
                                                                         boolean verbose) throws IOException {
        Map<String, Integer> eventMap = new HashMap<>();
        var eventNumber = 0;

        for (var event : events) {
            for (var settingDescriptor : event.getEventType().getSettingDescriptors()) {
                if (settingDescriptor.getDescription().equals(DURATION_EVENT) &&
                        new HashSet<>(event.getEventType().getCategoryNames())
                                .stream().anyMatch(categories::contains)) {
                    if (verbose) {
                        logAllCollectedEventsFromJFRFile(event);
                    }
                    getStatistics(event, eventMap);

                    XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                    serializer.addEventToTrace(convertedEvent);

                    eventNumber++;
                    break;
                }
            }
        }

        LOGGER.info("EVENT NUMBER = " + eventNumber);
        serializer.serializeLog(outputXesFilePath);

        if (showStatistics) {
            return Optional.of(eventMap);
        }
        return Optional.empty();
    }

    public Optional<Map<String, Integer>> processEventsFromFileFilteredByNames(String filePath,
                                                                               String outputXesFilePath,
                                                                               List<String> names,
                                                                               boolean showStatistics,
                                                                               boolean verbose) throws IOException {
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

            opt = saveToXESFilteredByNames(outputXesFilePath, names, events, converter, serializer, showStatistics, verbose);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return opt;
    }

    private Optional<Map<String, Integer>> saveToXESFilteredByNames(String outputXesFilePath, List<String> names,
                                                                    List<RecordedEvent> events, JFRToXESEventConverter converter,
                                                                    XESSerializerWrapper serializer,
                                                                    boolean showStatistics,
                                                                    boolean verbose) throws IOException {
        Map<String, Integer> eventMap = new HashMap<>();
        var eventNumber = 0;
        for (var event : events) {
            for (var settingDescriptor : event.getEventType().getSettingDescriptors()) {
                if (settingDescriptor.getDescription().equals(DURATION_EVENT) &&
                        names.contains(event.getEventType().getName())) {

                    if (verbose) {
                        logAllCollectedEventsFromJFRFile(event);
                    }
                    getStatistics(event, eventMap);

                    XEvent convertedEvent = converter.getConvertedEventFromJFRFile(event);
                    serializer.addEventToTrace(convertedEvent);

                    eventNumber++;
                    break;
                }
            }
        }

        LOGGER.info("EVENT NUMBER = " + eventNumber);
        serializer.serializeLog(outputXesFilePath);

        if (showStatistics) {
            return Optional.of(eventMap);
        }
        return Optional.empty();
    }

    private void logAllCollectedEventsFromJFRFile(RecordedEvent event) {
        LOGGER.info("Categories " + event.getEventType().getCategoryNames());
        LOGGER.info("Event: " + event.getEventType().getName());
    }
}
