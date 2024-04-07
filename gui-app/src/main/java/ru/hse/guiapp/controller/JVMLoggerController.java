package ru.hse.guiapp.controller;

import javafx.scene.control.Button;
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
}