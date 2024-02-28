package ru.hse.eventProcessing;

import ext.org.deckfour.xes.factory.XFactory;
import ext.org.deckfour.xes.factory.XFactoryBufferedImpl;
import ext.org.deckfour.xes.model.XAttribute;
import ext.org.deckfour.xes.model.XAttributeMap;
import ext.org.deckfour.xes.model.XAttributeTimestamp;
import ext.org.deckfour.xes.model.XEvent;
import ext.org.deckfour.xes.model.impl.XAttributeMapImpl;
import jdk.jfr.consumer.RecordedEvent;

import java.util.Date;

public class EventConverter {

    private final XFactory factory;

    public EventConverter() {
        this.factory = new XFactoryBufferedImpl();
    }

    // TODO: add more attributes if possible
    public XEvent getConvertedEventFromJFRFile(RecordedEvent jfrEvent) {
        XEvent event = factory.createEvent();
        XAttributeMap attributes = new XAttributeMapImpl();

        var time = jfrEvent.getStartTime();
        XAttributeTimestamp timeAttr = factory.createAttributeTimestamp("time:timestamp", Date.from(time), null);
        attributes.put("time:timestamp", timeAttr);

        var eventType = jfrEvent.getEventType().getName();
        XAttribute attributeEventType = factory.createAttributeLiteral("concept:name", eventType, null);
        attributes.put("concept:name", attributeEventType);

        long threadId;
        XAttribute attributeThreadId;
        if (jfrEvent.getThread() != null) {
            threadId = jfrEvent.getThread().getId();
            attributeThreadId = factory.createAttributeLiteral("Thread ID", String.valueOf(threadId), null);
        } else {
            attributeThreadId = factory.createAttributeLiteral("Thread ID", "null", null);
        }

        attributes.put("Thread ID", attributeThreadId);

        event.setAttributes(attributes);

        return event;
    }
}
