package ru.hse.eventProcessing;

import ext.org.deckfour.xes.extension.std.XConceptExtension;
import ext.org.deckfour.xes.extension.std.XTimeExtension;
import ext.org.deckfour.xes.factory.XFactory;
import ext.org.deckfour.xes.factory.XFactoryBufferedImpl;
import ext.org.deckfour.xes.model.XEvent;
import jdk.jfr.EventType;
import jdk.jfr.consumer.RecordedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void setUp() {
        factory = new XFactoryBufferedImpl();
        converter = new JFRToXESEventConverter();
    }

    @Test
    void testGetConvertedEventFromJFRFile() {
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

}