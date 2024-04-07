package ru.hse.guiapp.service;

import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.hse.api.CommandExecutor;

import java.io.File;

public class JVMLoggerService {

    public void openJarFileDialog(Stage stage, TextField inputFileField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR Files", "*.jar"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            inputFileField.setText(selectedFile.getAbsolutePath());
        }
    }

    public void openDirFileDialog(Stage stage, TextField inputDirField) {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            inputDirField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void executeNormalEventCollection(String jarInput,
                                             String jfrOutput,
                                             String recordingDuration,
                                             String xesOutput,
                                             String args,
                                             String settings,
                                             boolean showStatistics,
                                             boolean verbose) {
        CommandExecutor.normalEventCollection(jarInput, jfrOutput, recordingDuration, xesOutput,
                args, settings, showStatistics, verbose);
    }
}
