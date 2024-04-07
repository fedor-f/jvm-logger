package ru.hse.guiapp.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class JVMLoggerView {

    private GridPane grid;

    public JVMLoggerView() {
        init();
    }

    private void init() {
        grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        setInputFileField();
        setupDurationField();
    }

    private void setupDurationField() {
        Label durationLabel = new Label("-d, --duration:");
        grid.add(durationLabel, 0, 1);
        TextField durationField = new TextField();
        durationField.setPromptText("Recording Duration");
        grid.add(durationField, 1, 1);
    }

    private void setInputFileField() {
        Label inputFileLabel = new Label("-i, --input:");
        grid.add(inputFileLabel, 0, 3);
        TextField inputFileField = new TextField();
        inputFileField.setEditable(false);
        inputFileField.setPromptText("Input .jar file path");
        grid.add(inputFileField, 1, 3);
    }

    public Scene getScene() {
        return new Scene(grid);
    }
}
