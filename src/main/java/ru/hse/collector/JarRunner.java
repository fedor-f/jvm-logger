package ru.hse.collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class JarRunner {
    public void run(String pathToJar, String outputFilePath, String recordingDuration, String args) {
        try {

            ProcessBuilder pb = new ProcessBuilder("java",
                    String.format("-XX:StartFlightRecording=duration=%s,filename=%s", recordingDuration, outputFilePath),
                    "-jar",
                    pathToJar,
                    args
            );

            Process process = pb.start();

            process.waitFor();
        } catch (IOException | InterruptedException ignored) {
        }
    }

    private void printProcessOutput(Process process) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
    }
}
