package ru.hse.guiapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("JVM Logger GUI Application");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Input file chooser
        Label inputFileLabel = new Label("-i, --input:");
        grid.add(inputFileLabel, 0, 3);
        TextField inputFileField = new TextField();
        inputFileField.setEditable(false);
        inputFileField.setPromptText("Input .jar file path");
        grid.add(inputFileField, 1, 3);

        // Duration input field
        Label durationLabel = new Label("-d, --duration:");
        grid.add(durationLabel, 0, 1);
        TextField durationField = new TextField();
        durationField.setPromptText("Recording Duration");
        grid.add(durationField, 1, 1);

        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}