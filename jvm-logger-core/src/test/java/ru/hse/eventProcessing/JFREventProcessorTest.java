package ru.hse.eventProcessing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
class JFREventProcessorTest {

    @Test
    public void testProcessEventsFromFile(@TempDir Path tempDir) throws Exception {
        Path inputFilePath = Paths.get("src/main/resources/flight.jfr");
        Path outputFilePath = tempDir.resolve("output.xes");

        JFREventProcessor processor = new JFREventProcessor();
        Optional<Map<String, Integer>> result =
                processor.processEventsFromFile(inputFilePath.toString(),
                        outputFilePath.toString(),
                        true,
                        false);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().size());
    }

    @Test
    public void testProcessEventsFromFileByNames(@TempDir Path tempDir) throws Exception {
        Path inputFilePath = Paths.get("src/main/resources/flight.jfr");
        Path outputFilePath = tempDir.resolve("output.xes");

        JFREventProcessor processor = new JFREventProcessor();
        Optional<Map<String, Integer>> result =
                processor.processEventsFromFileFilteredByNames(inputFilePath.toString(),
                        outputFilePath.toString(),
                        List.of("jdk.ActiveRecording"),
                        true,
                        false);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    public void testProcessEventsFromFileByCategories(@TempDir Path tempDir) throws Exception {
        Path inputFilePath = Paths.get("src/main/resources/flight.jfr");
        Path outputFilePath = tempDir.resolve("output.xes");

        JFREventProcessor processor = new JFREventProcessor();
        Optional<Map<String, Integer>> result =
                processor.processEventsFromFileFilteredByCategories(inputFilePath.toString(),
                        outputFilePath.toString(),
                        List.of("GC"),
                        true,
                        false);

        assertTrue(result.isPresent());
        assertEquals(0, result.get().size());
    }

    @Test
    public void testProcessEventsFromFileNoStat(@TempDir Path tempDir) throws Exception {
        Path inputFilePath = Paths.get("src/main/resources/flight.jfr");
        Path outputFilePath = tempDir.resolve("output.xes");

        JFREventProcessor processor = new JFREventProcessor();
        Optional<Map<String, Integer>> result =
                processor.processEventsFromFile(inputFilePath.toString(),
                        outputFilePath.toString(),
                        false,
                        false);

        assertFalse(result.isPresent());
    }

    @Test
    public void testProcessEventsFromFileByNamesNoStat(@TempDir Path tempDir) throws Exception {
        Path inputFilePath = Paths.get("src/main/resources/flight.jfr");
        Path outputFilePath = tempDir.resolve("output.xes");

        JFREventProcessor processor = new JFREventProcessor();
        Optional<Map<String, Integer>> result =
                processor.processEventsFromFileFilteredByNames(inputFilePath.toString(),
                        outputFilePath.toString(),
                        List.of("jdk.ActiveRecording"),
                        false,
                        false);

        assertFalse(result.isPresent());
    }

    @Test
    public void testProcessEventsFromFileByCategoriesNoStat(@TempDir Path tempDir) throws Exception {
        Path inputFilePath = Paths.get("src/main/resources/flight.jfr");
        Path outputFilePath = tempDir.resolve("output.xes");

        JFREventProcessor processor = new JFREventProcessor();
        Optional<Map<String, Integer>> result =
                processor.processEventsFromFileFilteredByCategories(inputFilePath.toString(),
                        outputFilePath.toString(),
                        List.of("GC"),
                        false,
                        false);

        assertFalse(result.isPresent());
    }
}