package ru.hse.guiapp.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ru.hse.guiapp.controller.JVMLoggerController;


public class JVMLoggerView {

    private GridPane grid;

    private final JVMLoggerController controller;

    public JVMLoggerView(Stage stage) {
        controller = new JVMLoggerController();
        init(stage);
    }

    private void init(Stage stage) {
        grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        setInputFileField(stage);
        setupDurationField();
    }

    private void setupDurationField() {
        Label durationLabel = new Label("Event recording duration:");
        grid.add(durationLabel, 0, 1);
        TextField durationField = new TextField();
        durationField.setPromptText("example: 2s, 1m10s");
        grid.add(durationField, 1, 1);
    }

    private void setInputFileField(Stage stage) {
        Label inputFileLabel = new Label("Input .jar file path:");
        grid.add(inputFileLabel, 0, 3);
        TextField inputFileField = new TextField();
        inputFileField.setEditable(false);
        grid.add(inputFileField, 1, 3);

        Button inputFileButton = new Button("Browse...");

        controller.chooseJar(inputFileButton, inputFileField, stage);

        grid.add(inputFileButton, 2, 3);
    }

    public Scene getScene() {
        return new Scene(grid);
    }
}
