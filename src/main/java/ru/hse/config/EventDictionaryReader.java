package ru.hse.config;

import ru.hse.config.entity.EventDescription;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventDictionaryReader {

    private static final String EVENT_DICTIONARY_FILE_NAME = "event_dictionary_filtered.csv";

    public List<EventDescription> readEventDictionary() throws IOException {
        List<EventDescription> result = new ArrayList<>();

        try (var bufferedReader = new BufferedReader(new FileReader(EVENT_DICTIONARY_FILE_NAME))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                var splittedElements = line.split(",");
                result.add(new EventDescription(splittedElements[0], splittedElements[1], splittedElements[2], splittedElements[3]));
            }
        }

        return result;
    }
}
