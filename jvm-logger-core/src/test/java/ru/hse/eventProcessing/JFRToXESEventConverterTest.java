package ru.hse.eventProcessing;

import ext.org.deckfour.xes.extension.std.XConceptExtension;
import ext.org.deckfour.xes.extension.std.XTimeExtension;
import ext.org.deckfour.xes.factory.XFactory;
import ext.org.deckfour.xes.factory.XFactoryBufferedImpl;
import ext.org.deckfour.xes.model.XEvent;
import jdk.jfr.EventType;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JFRToXESEventConverterTest {

    private XFactoryBufferedImpl factory;

    @Mock
    private XEvent event;

    @Mock
    private RecordedEvent jfrEvent;

    @Mock
    private EventType eventType;

    private JFRToXESEventConverter converter;

    @BeforeEach
    public void setUp() {
        factory = new XFactoryBufferedImpl();
        converter = new JFRToXESEventConverter();
    }

    @Test
    public void testGetConvertedEventFromJFRFile() {
        var time = Instant.now();
        when(jfrEvent.getStartTime()).thenReturn(time);
        when(jfrEvent.getEventType()).thenReturn(eventType);
        when(eventType.getName()).thenReturn("Test Event");

        XEvent xEvent = converter.getConvertedEventFromJFRFile(jfrEvent);

        assertNotNull(xEvent);

        ZonedDateTime zonedDateTime = time.atZone(ZoneId.of("Europe/Moscow"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        String formattedTimestamp = zonedDateTime.format(formatter);

        assertEquals(xEvent.getAttributes().get("time:timestamp").toString(), formattedTimestamp);
        assertTrue(xEvent.getExtensions().contains(XTimeExtension.instance()));

        assertEquals(xEvent.getAttributes().get("concept:name").toString(), "Test Event");
        assertTrue(xEvent.getExtensions().contains(XConceptExtension.instance()));
    }

    @Test
    public void testRealEventsConverting() {
        Path path = Paths.get("src/main/resources/flight.jfr");

        RecordedEvent eventMonitor = null;
        try (RecordingFile recordingFile = new RecordingFile(path)) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();

                if (event.getEventType().getName().equals("jdk.JavaMonitorWait")) {
                    eventMonitor = event;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        XEvent xEvent = converter.getConvertedEventFromJFRFile(eventMonitor);

        assertTrue(xEvent.getAttributes().containsKey("duration"));
        assertTrue(xEvent.getAttributes().containsKey("eventThread.name"));
        assertTrue(xEvent.getAttributes().containsKey("eventThread.id"));
        assertTrue(xEvent.getAttributes().containsKey("monitorClass.name"));
        assertTrue(xEvent.getAttributes().containsKey("timeout"));
        assertTrue(xEvent.getAttributes().containsKey("timedOut"));
        assertTrue(xEvent.getAttributes().containsKey("address"));
    }

    @Test
    public void testActiveSettingEventAttrs() {
        Path path = Paths.get("src/main/resources/flight.jfr");

        RecordedEvent eventActiveSetting = null;
        try (RecordingFile recordingFile = new RecordingFile(path)) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();

                if (event.getEventType().getName().equals("jdk.ActiveSetting")) {
                    eventActiveSetting = event;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        XEvent xEvent = converter.getConvertedEventFromJFRFile(eventActiveSetting);

        System.out.println(xEvent.getAttributes());
        assertTrue(xEvent.getAttributes().containsKey("duration"));
        assertTrue(xEvent.getAttributes().containsKey("eventThread.name"));
        assertTrue(xEvent.getAttributes().containsKey("eventThread.id"));
        assertTrue(xEvent.getAttributes().containsKey("name"));
        assertTrue(xEvent.getAttributes().containsKey("value"));
    }

    @Test
    public void testActiveRecordingEventAttrs() {
        Path path = Paths.get("src/main/resources/flight.jfr");

        RecordedEvent eventActiveRecording = null;
        try (RecordingFile recordingFile = new RecordingFile(path)) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();

                if (event.getEventType().getName().equals("jdk.ActiveRecording")) {
                    eventActiveRecording = event;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        XEvent xEvent = converter.getConvertedEventFromJFRFile(eventActiveRecording);

        assertTrue(xEvent.getAttributes().containsKey("duration"));
        assertTrue(xEvent.getAttributes().containsKey("eventThread.name"));
        assertTrue(xEvent.getAttributes().containsKey("eventThread.id"));
        assertTrue(xEvent.getAttributes().containsKey("id"));
        assertTrue(xEvent.getAttributes().containsKey("name"));
        assertTrue(xEvent.getAttributes().containsKey("destination"));
        assertTrue(xEvent.getAttributes().containsKey("maxAge"));
        assertTrue(xEvent.getAttributes().containsKey("flushInterval"));
        assertTrue(xEvent.getAttributes().containsKey("flushInterval"));
        assertTrue(xEvent.getAttributes().containsKey("maxSize"));
        assertTrue(xEvent.getAttributes().containsKey("recordingStart"));
        assertTrue(xEvent.getAttributes().containsKey("recordingDuration"));
    }
}