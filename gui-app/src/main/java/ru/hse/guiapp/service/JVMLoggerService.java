package ru.hse.guiapp.service;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.hse.api.CommandExecutor;
import ru.hse.guiapp.config.EventInfo;
import ru.hse.guiapp.util.JoinMapUtil;

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
                                             boolean verbose,
                                             TextArea textField) {
        textField.clear();
        textField.setText("Executing .jar...");

        var optMap = CommandExecutor.normalEventCollection(jarInput, jfrOutput, recordingDuration, xesOutput,
                args, settings, showStatistics, verbose);

        textField.appendText("\nEvents collected successfully\n");

        optMap.ifPresent(stringIntegerMap -> {
            var map = JoinMapUtil.innerJoin(EventInfo.EVENT_NAME_MAP, stringIntegerMap);

            textField.appendText("STATISTICS:\n\n");
            textField.appendText("Event Name\tFrequency\tDescription\n");
            map.forEach((key, value) -> {
                textField.appendText(key + "\t" + value.first() + "\t" + value.second() + "\n");
            });
        });
    }
}
