package ru.hse.XESprocessor;

import ext.org.deckfour.xes.extension.std.XConceptExtension;
import ext.org.deckfour.xes.extension.std.XTimeExtension;
import ext.org.deckfour.xes.factory.XFactory;
import ext.org.deckfour.xes.factory.XFactoryBufferedImpl;
import ext.org.deckfour.xes.model.XAttributeMap;
import ext.org.deckfour.xes.model.XEvent;
import ext.org.deckfour.xes.model.XLog;
import ext.org.deckfour.xes.model.XTrace;
import ext.org.deckfour.xes.model.impl.XAttributeMapImpl;
import ext.org.deckfour.xes.model.impl.XTraceImpl;
import ext.org.deckfour.xes.out.XSerializer;
import ext.org.deckfour.xes.out.XesXmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class XESSerializerWrapper {
    private final XLog log;

    private final XTrace trace;

    private final XSerializer serializer;

    private static final Logger LOGGER = Logger.getLogger(XESSerializerWrapper.class.getName());

    public XESSerializerWrapper() {
        XFactory factory = new XFactoryBufferedImpl();
        this.log = factory.createLog();

        this.log.getExtensions().add(XConceptExtension.instance());
        this.log.getExtensions().add(XTimeExtension.instance());

        this.serializer = new XesXmlSerializer();

        XAttributeMap attributesTrace = new XAttributeMapImpl();
        // mock
        attributesTrace.put("concept:name", factory.createAttributeLiteral("concept:name", "Event Name", null));
        this.trace = new XTraceImpl(attributesTrace);
    }

    public void addEventToTrace(XEvent event) {
        trace.add(event);
    }

    public void serializeLog(String path) throws IOException {
        LOGGER.info("The size of serializing trace is: " + trace.getAttributes().size());
        log.add(trace);
        serializer.serialize(log, new FileOutputStream(path));
    }
}
