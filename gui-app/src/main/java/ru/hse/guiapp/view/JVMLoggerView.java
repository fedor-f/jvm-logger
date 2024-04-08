package ru.hse.guiapp.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import ru.hse.guiapp.config.EventInfo;
import ru.hse.guiapp.controller.JVMLoggerController;


public class JVMLoggerView {

    private GridPane grid;

    private TextField inputFileField;

    private TextField durationField;

    private TextField outputFileField;

    private TextField argsField;

    private TextArea statsArea;

    private VBox vBox;

    private Button stopButton;

    private Button executeButton;

    private CheckBox filterByCategoriesCheckbox;

    private CheckBox filterByNamesCheckbox;

    private CheckComboBox<String> choiceBoxNames;

    private CheckComboBox<String> choiceBoxCategories;

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

        executeButton = new Button("Execute");
        stopButton = new Button("Interrupt");

        filterByCategoriesCheckbox = new CheckBox("Filter events by categories?");
        filterByNamesCheckbox = new CheckBox("Filter events by event type names?");

        ObservableList<String> list = FXCollections.observableArrayList(EventInfo.EVENT_NAME_LIST.keySet());
        choiceBoxNames = new CheckComboBox<>(list);
        ObservableList<String> list2 = FXCollections.observableArrayList(EventInfo.CATEGORY_LIST);
        choiceBoxCategories = new CheckComboBox<>(list2);

        choiceBoxNames.setVisible(false);
        choiceBoxCategories.setVisible(false);

        grid.add(choiceBoxCategories, 1, 5);
        grid.add(choiceBoxNames, 1, 6);

        grid.add(filterByCategoriesCheckbox, 0, 5);
        grid.add(filterByNamesCheckbox, 0, 6);

        setInputFileField(stage);
        setupDurationField();
        setupOutputFilePathField(stage);
        setupJarArgsField();
        setupStatisticsArea();
        setupStopButton();
        setupExecuteButton();
        setupCheckboxFilterNames();
        setupCheckboxFilterCategories();
    }

    private void setupCheckboxFilterNames() {
        filterByNamesCheckbox.setOnAction(e -> {
            if (filterByNamesCheckbox.isSelected()) {
                choiceBoxCategories.setVisible(false);
                choiceBoxNames.setVisible(true);

                filterByCategoriesCheckbox.setSelected(false);
            } else {
                choiceBoxNames.setVisible(false);
            }
        });
    }

    private void setupCheckboxFilterCategories() {
        filterByCategoriesCheckbox.setOnAction(e -> {
            if (filterByCategoriesCheckbox.isSelected()) {
                choiceBoxCategories.setVisible(true);
                choiceBoxNames.setVisible(false);

                filterByNamesCheckbox.setSelected(false);
            } else {
                choiceBoxCategories.setVisible(false);
            }
        });
    }

    private void setupExecuteButton() {
        controller.executeNormalEventCollection(executeButton,
                inputFileField.getText(),
                // TODO: remove mock
                "/Users/fedorfilippov/Desktop/logger/flight.jfr",
                durationField,
                outputFileField,
                argsField.getText(),
                "",
                statsArea,
                stopButton
        );

        grid.add(executeButton, 1, 7);
    }

    private void setupStopButton() {
        stopButton.setDisable(true);

        controller.interruptExecution(stopButton, executeButton);

        grid.add(stopButton, 2, 7);
    }

    private void setupStatisticsArea() {
        statsArea = new TextArea();
        statsArea.setEditable(false);
        vBox = new VBox();
        vBox.getChildren().addAll(grid, statsArea);
    }

    private void setupJarArgsField() {
        Label argsLabel = new Label("String arguments for .jar");
        grid.add(argsLabel, 0, 0);
        argsField = new TextField();
        grid.add(argsField, 1, 0);
    }

    private void setupOutputFilePathField(Stage stage) {
        Label outputFileLabel = new Label(".xes File output path");
        grid.add(outputFileLabel, 0, 2);
        outputFileField = new TextField();
        outputFileField.setPromptText("Default path is current directory");
        grid.add(outputFileField, 1, 2);

        Button inputFileButton = new Button("Browse...");
        outputFileField.setEditable(false);

        controller.chooseDirectory(inputFileButton, outputFileField, stage);

        grid.add(inputFileButton, 2, 2);
    }

    private void setupDurationField() {
        Label durationLabel = new Label("Event recording duration:");
        grid.add(durationLabel, 0, 1);
        durationField = new TextField();
        durationField.setPromptText("example: 2s, 1m10s");
        grid.add(durationField, 1, 1);
    }

    private void setInputFileField(Stage stage) {
        Label inputFileLabel = new Label("Input .jar file path:");
        grid.add(inputFileLabel, 0, 3);
        inputFileField = new TextField();
        inputFileField.setEditable(false);
        grid.add(inputFileField, 1, 3);

        Button inputFileButton = new Button("Browse...");

        controller.chooseJar(inputFileButton, inputFileField, stage);

        grid.add(inputFileButton, 2, 3);
    }

    public Scene getScene() {
        return new Scene(vBox);
    }
}
