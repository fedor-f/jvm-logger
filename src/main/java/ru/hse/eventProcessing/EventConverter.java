package ru.hse.eventProcessing;

import ext.org.deckfour.xes.factory.XFactory;
import ext.org.deckfour.xes.factory.XFactoryBufferedImpl;
import ext.org.deckfour.xes.model.XAttribute;
import ext.org.deckfour.xes.model.XAttributeMap;
import ext.org.deckfour.xes.model.XAttributeTimestamp;
import ext.org.deckfour.xes.model.XEvent;
import ext.org.deckfour.xes.model.impl.XAttributeMapImpl;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.consumer.RecordedEvent;

import java.util.Date;
import java.util.List;

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

        processEventFields(jfrEvent, attributes);

        event.setAttributes(attributes);

        return event;
    }

    private void processEventFields(RecordedEvent event, XAttributeMap attributes) {
        List<ValueDescriptor> fields = event.getFields();

        for (ValueDescriptor field : fields) {
            if (field.getName().equals("startTime")) {
                continue;
            }
            Object value = event.getValue(field.getName());

            if (checkIfTypeNamePrimitive(field.getTypeName())) {
                XAttribute attributeEventType = factory.createAttributeLiteral(field.getName(), value.toString(), null);
                attributes.put(field.getName(), attributeEventType);
            }

            // TODO: add processing of complex types
        }
    }

    private boolean checkIfTypeNamePrimitive(String name) {
        return name.equals("long") || name.equals("int") || name.equals("float")
                || name.equals("boolean") || name.equals("double") || name.equals("char");
    }
}
