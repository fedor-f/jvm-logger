package ru.hse.guiapp.controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.hse.guiapp.service.JVMLoggerService;

public class JVMLoggerController {

    private final JVMLoggerService service;

    public JVMLoggerController() {
        service = new JVMLoggerService();
    }

    public void chooseJar(Button inputFileButton, TextField inputFileField, Stage stage) {
        inputFileButton.setOnAction(e -> service.openJarFileDialog(stage, inputFileField));
    }

    public void chooseDirectory(Button inputDirButton, TextField inputDirField, Stage stage) {
        inputDirButton.setOnAction(e -> service.openDirFileDialog(stage, inputDirField));
    }

    public void executeNormalEventCollection(Button executeButton,
                                             String jarInput,
                                             String jfrOutput,
                                             TextField recordingDuration,
                                             TextField xesOutput,
                                             String args,
                                             String settings,
                                             TextArea textField) {
        executeButton.setOnAction(e -> {
            service.executeNormalEventCollection(jarInput, jfrOutput, recordingDuration.getText(), xesOutput.getText() + "/output.xes",
                    args, settings, true, false, textField);
        });
    }
}