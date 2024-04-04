package ru.hse.eventProcessing;

import ext.org.deckfour.xes.extension.std.XConceptExtension;
import ext.org.deckfour.xes.extension.std.XTimeExtension;
import ext.org.deckfour.xes.factory.XFactory;
import ext.org.deckfour.xes.factory.XFactoryBufferedImpl;
import ext.org.deckfour.xes.model.XAttribute;
import ext.org.deckfour.xes.model.XAttributeMap;
import ext.org.deckfour.xes.model.XAttributeTimestamp;
import ext.org.deckfour.xes.model.XEvent;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedObject;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventConverter {

    private final XFactory factory;

    public EventConverter() {
        this.factory = new XFactoryBufferedImpl();
    }

    public XEvent getConvertedEventFromJFRFile(RecordedEvent jfrEvent) {
        XEvent event = factory.createEvent();
        XAttributeMap attributes = new XAttributeLinkedMapImpl();

        addTimestampAndConceptNameAttributes(jfrEvent, attributes);

        processEventFields(jfrEvent, attributes);

        event.setAttributes(attributes);

        return event;
    }

    private void addTimestampAndConceptNameAttributes(RecordedEvent jfrEvent, XAttributeMap attributes) {
        var time = jfrEvent.getStartTime();
        XAttributeTimestamp timeAttr = factory.createAttributeTimestamp("time:timestamp", Date.from(time), XTimeExtension.instance());
        attributes.put("time:timestamp", timeAttr);

        var eventType = jfrEvent.getEventType().getName();
        XAttribute attributeEventType = factory.createAttributeLiteral("concept:name", eventType, XConceptExtension.instance());
        attributes.put("concept:name", attributeEventType);
    }

    private void processEventFields(RecordedEvent event, XAttributeMap attributes) {
        List<ValueDescriptor> fields = event.getFields();

        for (ValueDescriptor field : fields) {
            if (field.getName().equals("startTime")) {
                continue;
            }

            if (checkIfTypeNamePrimitiveOrString(field.getTypeName())) {
                Object primitiveValue = event.getValue(field.getName());
                XAttribute attributeEventType;
                if (primitiveValue != null) {
                    attributeEventType = factory.createAttributeLiteral(field.getName(), primitiveValue.toString(), null);
                } else {
                    attributeEventType = factory.createAttributeLiteral(field.getName(), "null", null);
                }
                attributes.put(field.getName(), attributeEventType);
            } else if (field.getTypeName().equals("jdk.types.Package")) {
                RecordedObject value = event.getValue(field.getName());
                addPackageAttribute(attributes, value);
            } else if (field.getTypeName().equals("jdk.types.Module")) {
                RecordedObject value = event.getValue(field.getName());
                if (value != null) {
                    addModuleAttributes(attributes, value, "module");
                }
            } else if (field.getTypeName().equals("java.lang.Thread")) {
                RecordedObject value = event.getValue(field.getName());
                if (value != null) {
                    addThreadAttributes(attributes, value);
                }
            } else if (field.getTypeName().equals("java.lang.Class")) {
                RecordedObject value = event.getValue(field.getName());
                if (value != null) {
                    addClassAttributes(attributes, value);
                }
            }
            // TODO: add processing for java.lang.Class
        }
    }

    private void addClassAttributes(XAttributeMap attributes, RecordedObject value) {
        String className = value.getValue("name");
        XAttribute attributeEventClassName = factory.createAttributeLiteral("class.name", Objects.requireNonNullElse(className, "null"), null);
        attributes.put("class.name", attributeEventClassName);
    }

    private void addThreadAttributes(XAttributeMap attributes, RecordedObject value) {
        String threadName = value.getValue("javaName");
        XAttribute attributeEventThreadName = factory.createAttributeLiteral("eventThread.name", Objects.requireNonNullElse(threadName, "null"), null);
        attributes.put("eventThread.name", attributeEventThreadName);

        Long threadId = value.getValue("javaThreadId");
        XAttribute attributeEventThreadId = factory.createAttributeLiteral("eventThread.id", Objects.requireNonNullElse(String.valueOf(threadId), "null"), null);
        attributes.put("eventThread.id", attributeEventThreadId);
    }

    private void addPackageAttribute(XAttributeMap attributes, RecordedObject value) {
        XAttribute attributePackageName = factory.createAttributeLiteral("package.name", value.getValue("name"), null);
        attributes.put("package.name", attributePackageName);

        RecordedObject module = value.getValue("module");
        if (module != null) {
            addModuleAttributes(attributes, module, "package.module");
        }

        Boolean exported = value.getValue("exported");
        XAttribute attributePackageExported = factory.createAttributeLiteral("package.exported", exported.toString(), null);
        attributes.put("package.exported", attributePackageExported);
    }

    private void addModuleAttributes(XAttributeMap attributes, RecordedObject module, String attributeStringPrefix) {
        String moduleName = module.getValue("name");
        XAttribute attributeModuleName = factory.createAttributeLiteral(String.format("%s.name", attributeStringPrefix), Objects.requireNonNullElse(moduleName, "null"), null);
        attributes.put("package.module.name", attributeModuleName);

        String version = module.getValue("version");
        XAttribute attributeModuleVersion = factory.createAttributeLiteral(String.format("%s.version", attributeStringPrefix), Objects.requireNonNullElse(version, "null"), null);
        attributes.put("package.module.version", attributeModuleVersion);

        String location = module.getValue("location");
        XAttribute attributeModuleLocation = factory.createAttributeLiteral(String.format("%s.location", attributeStringPrefix), Objects.requireNonNullElse(location, "null"), null);
        attributes.put("package.module.location", attributeModuleLocation);

        RecordedObject classLoader = module.getValue("classLoader");
        String classLoaderName = classLoader.getValue("name");
        XAttribute attributeModuleClassLoader = factory.createAttributeLiteral(String.format("%s.classLoader.name", attributeStringPrefix), Objects.requireNonNullElse(classLoaderName, "null"), null);
        attributes.put("package.module.classLoader.name", attributeModuleClassLoader);
    }

    private boolean checkIfTypeNamePrimitiveOrString(String name) {
        return name.equals("long") || name.equals("int") || name.equals("float")
                || name.equals("boolean") || name.equals("double") || name.equals("char") || name.equals("java.lang.String");
    }
}
