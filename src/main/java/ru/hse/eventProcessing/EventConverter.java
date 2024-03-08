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
import jdk.jfr.consumer.RecordedObject;
import ru.hse.eventProcessing.entity.Module;
import ru.hse.eventProcessing.entity.Package;

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
        XAttributeMap attributes = new XAttributeMapImpl();

        addTimestampAndConceptNameAttributes(jfrEvent, attributes);

        processEventFields(jfrEvent, attributes);

        event.setAttributes(attributes);

        return event;
    }

    private void addTimestampAndConceptNameAttributes(RecordedEvent jfrEvent, XAttributeMap attributes) {
        var time = jfrEvent.getStartTime();
        XAttributeTimestamp timeAttr = factory.createAttributeTimestamp("time:timestamp", Date.from(time), null);
        attributes.put("time:timestamp", timeAttr);

        var eventType = jfrEvent.getEventType().getName();
        XAttribute attributeEventType = factory.createAttributeLiteral("concept:name", eventType, null);
        attributes.put("concept:name", attributeEventType);
    }

    private void processEventFields(RecordedEvent event, XAttributeMap attributes) {
        List<ValueDescriptor> fields = event.getFields();

        for (ValueDescriptor field : fields) {
            if (field.getName().equals("startTime")) {
                continue;
            }

            if (checkIfTypeNamePrimitive(field.getTypeName())) {
                Object primitiveValue = event.getValue(field.getName());
                XAttribute attributeEventType = factory.createAttributeLiteral(field.getName(), primitiveValue.toString(), null);
                attributes.put(field.getName(), attributeEventType);
            } else if (field.getTypeName().equals("jdk.types.Package")) {
                RecordedObject value = event.getValue(field.getName());
                addPackageAttribute(attributes, value);
            } else if (field.getTypeName().equals("jdk.types.Module")) {
                RecordedObject value = event.getValue(field.getName());
                if (value != null) {
                    addModuleAttributes(attributes, value);
                }
            }
//            else if (field.getTypeName().equals("java.lang.Thread")) {
//                addThreadAttributes(attributes, (Thread) value);
//            }

            // TODO: add processing for java.lang.Class
        }
    }

    private void addThreadAttributes(XAttributeMap attributes, Thread value) {
        XAttribute attributeEventThreadName = factory.createAttributeLiteral("eventThread.name", value.getName(), null);
        attributes.put("eventThread.name", attributeEventThreadName);

        XAttribute attributeEventThreadId = factory.createAttributeLiteral("eventThread.id", String.valueOf(value.getId()), null);
        attributes.put("eventThread.id", attributeEventThreadId);

        XAttribute attributeEventThreadState = factory.createAttributeLiteral("eventThread.state", value.getState().name(), null);
        attributes.put("eventThread.state", attributeEventThreadState);

        // TODO: add thread group processing
    }

    private void addPackageAttribute(XAttributeMap attributes, RecordedObject value) {
        XAttribute attributePackageName = factory.createAttributeLiteral("package.name", value.getValue("name"), null);
        attributes.put("package.name", attributePackageName);

        RecordedObject module = value.getValue("module");
        if (module != null) {
            addModuleAttributes(attributes, module);
        }

        Boolean exported = value.getValue("exported");
        XAttribute attributePackageExported = factory.createAttributeLiteral("package.exported", exported.toString(), null);
        attributes.put("package.exported", attributePackageExported);
    }

    private void addModuleAttributes(XAttributeMap attributes, RecordedObject module) {
        String moduleName = module.getValue("name");
        XAttribute attributeModuleName = factory.createAttributeLiteral("package.module.name", Objects.requireNonNullElse(moduleName, "null"), null);
        attributes.put("package.module.name", attributeModuleName);

        String version = module.getValue("version");
        XAttribute attributeModuleVersion = factory.createAttributeLiteral("package.module.version", Objects.requireNonNullElse(version, "null"), null);
        attributes.put("package.module.version", attributeModuleVersion);

        String location = module.getValue("location");
        XAttribute attributeModuleLocation = factory.createAttributeLiteral("package.module.location", Objects.requireNonNullElse(location, "null"), null);
        attributes.put("package.module.location", attributeModuleLocation);

        RecordedObject classLoader = module.getValue("classLoader");
        String classLoaderName = classLoader.getValue("name");
        XAttribute attributeModuleClassLoader = factory.createAttributeLiteral("package.module.classLoader.name", Objects.requireNonNullElse(classLoaderName, "null"), null);
        attributes.put("package.module.classLoader.name", attributeModuleClassLoader);
    }

    private boolean checkIfTypeNamePrimitive(String name) {
        return name.equals("long") || name.equals("int") || name.equals("float")
                || name.equals("boolean") || name.equals("double") || name.equals("char");
    }
}
