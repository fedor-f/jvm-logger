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
import ru.hse.eventProcessing.entity.Module;
import ru.hse.eventProcessing.entity.Package;

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
            } else if (field.getTypeName().equals("jdk.types.Package")) {
                addPackageAttribute(attributes, (Package) value);
            } else if (field.getTypeName().equals("jdk.types.Module")) {
                addModuleAttributes(attributes, (Module) value);
            } else if (field.getTypeName().equals("java.lang.Thread")) {
                addThreadAttributes(attributes, (Thread) value);
            }

            // TODO: add processing for java.lang.Class
        }
    }

    private void addThreadAttributes(XAttributeMap attributes, Thread value) {
        XAttribute attributeEventThreadName = factory.createAttributeLiteral("eventThread.osName", value.getName(), null);
        attributes.put("eventThread.name", attributeEventThreadName);

        XAttribute attributeEventThreadId = factory.createAttributeLiteral("eventThread.id", String.valueOf(value.getId()), null);
        attributes.put("eventThread.id", attributeEventThreadId);

        XAttribute attributeEventThreadState = factory.createAttributeLiteral("eventThread.state", value.getState().name(), null);
        attributes.put("eventThread.state", attributeEventThreadState);

        // TODO: add thread group processing
    }

    private void addPackageAttribute(XAttributeMap attributes, Package value) {
        XAttribute attributePackageName = factory.createAttributeLiteral("package.name", value.getName(), null);
        attributes.put("package.name", attributePackageName);

        Module module = value.getModule();
        addModuleAttributes(attributes, module);

        XAttribute attributePackageExported = factory.createAttributeLiteral("package.exported", value.getExported().toString(), null);
        attributes.put("package.exported", attributePackageExported);
    }

    private void addModuleAttributes(XAttributeMap attributes, Module module) {
        XAttribute attributeModuleName = factory.createAttributeLiteral("package.module.name", module.getName(), null);
        attributes.put("package.module.name", attributeModuleName);

        XAttribute attributeModuleVersion = factory.createAttributeLiteral("package.module.version", module.getVersion(), null);
        attributes.put("package.module.version", attributeModuleVersion);

        XAttribute attributeModuleLocation = factory.createAttributeLiteral("package.module.location", module.getLocation(), null);
        attributes.put("package.module.location", attributeModuleLocation);

        XAttribute attributeModuleClassLoader = factory.createAttributeLiteral("package.module.classLoader.name", module.getClassLoader().getName(), null);
        attributes.put("package.module.classLoader.name", attributeModuleClassLoader);
    }

    private boolean checkIfTypeNamePrimitive(String name) {
        return name.equals("long") || name.equals("int") || name.equals("float")
                || name.equals("boolean") || name.equals("double") || name.equals("char");
    }
}
