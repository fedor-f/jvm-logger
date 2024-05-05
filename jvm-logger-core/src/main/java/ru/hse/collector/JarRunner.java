package ru.hse.collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JarRunner {
    public void run(String pathToJar, String outputFilePath, String recordingDuration, String args) {
        String startFlightRecording;

        startFlightRecording = String.format("-XX:StartFlightRecording=duration=%s,filename=%s", recordingDuration, outputFilePath);

        try {
            ProcessBuilder pb = new ProcessBuilder("java",
                    startFlightRecording,
                    "-jar",
                    pathToJar,
                    args
            );

            Process process = pb.start();

            process.waitFor();
        } catch (IOException | InterruptedException ignored) {
        }
    }
}
