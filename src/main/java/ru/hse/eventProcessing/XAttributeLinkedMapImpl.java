package ru.hse.eventProcessing;

import ext.org.deckfour.xes.model.XAttribute;
import ext.org.deckfour.xes.model.XAttributeMap;
import ext.org.deckfour.xes.model.impl.XAttributeMapImpl;

import java.util.LinkedHashMap;
import java.util.Map;

public class XAttributeLinkedMapImpl extends LinkedHashMap<String, XAttribute> implements XAttributeMap {

    public XAttributeLinkedMapImpl() {
        this(0);
    }

    public XAttributeLinkedMapImpl(int size) {
        super(size);
    }

    public XAttributeLinkedMapImpl(Map<String,XAttribute> template) {
        super(template.size());
        for(String key : template.keySet()) {
            put(key, template.get(key));
        }
    }

    public Object clone() {
        XAttributeMapImpl clone = (XAttributeMapImpl)super.clone();
        clone.clear();
        for(String key : this.keySet()) {
            clone.put(key, (XAttribute)this.get(key).clone());
        }
        return clone;
    }
}
