package ru.hse.guiapp.controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import ru.hse.guiapp.model.EventStatistic;
import ru.hse.guiapp.service.JVMLoggerService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class JVMLoggerController {

    private static final String DOC_LINK = "https://github.com/fedor-f/jvm-logger/blob/main/docs/Event%20Documentation.pdf";

    private final JVMLoggerService service;

    private Service<Void> task;

    public JVMLoggerController() {
        service = new JVMLoggerService();
    }

    public void chooseJar(Button inputFileButton, TextField inputFileField, Stage stage) {
        inputFileButton.setOnAction(e -> service.openJarFileDialog(stage, inputFileField));
    }

    public void chooseDirectory(Button inputDirButton, TextField inputDirField, Stage stage) {
        inputDirButton.setOnAction(e -> service.openDirFileDialog(stage, inputDirField));
    }

    public void executeButtonLogic(Button executeButton,
                                   TextField jarInput,
                                   String jfrOutput,
                                   TextField recordingDuration,
                                   TextField xesOutput,
                                   TextField args,
                                   String settings,
                                   TextArea textField,
                                   Button stopButton,
                                   TableView<EventStatistic> tableView,
                                   CheckBox checkboxNames,
                                   CheckBox checkboxCategories,
                                   CheckComboBox<String> comboBoxNames,
                                   CheckComboBox<String> comboBoxCategories) {

        executeButton.setOnAction(e -> {
            if (!checkboxNames.isSelected() && !checkboxCategories.isSelected()) {
                handleNormalEventExecution(executeButton,
                        jarInput,
                        jfrOutput,
                        recordingDuration,
                        xesOutput,
                        args,
                        settings,
                        textField,
                        stopButton,
                        tableView
                );

                return;
            }

            if (checkboxNames.isSelected()) {
                handleEventExecutionWhenFilteringByNames(executeButton,
                        jarInput,
                        jfrOutput,
                        recordingDuration,
                        xesOutput,
                        args,
                        settings,
                        textField,
                        stopButton,
                        tableView,
                        comboBoxNames
                );

                return;
            }

            if (checkboxCategories.isSelected()) {
                handleEventExecutionWhenFilteringByCategories(
                        executeButton,
                        jarInput,
                        jfrOutput,
                        recordingDuration,
                        xesOutput,
                        args,
                        settings,
                        textField,
                        stopButton,
                        tableView,
                        comboBoxCategories
                );
            }
        });
    }

    private void handleEventExecutionWhenFilteringByCategories(Button executeButton,
                                                               TextField jarInput,
                                                               String jfrOutput,
                                                               TextField recordingDuration,
                                                               TextField xesOutput,
                                                               TextField args,
                                                               String settings,
                                                               TextArea textField,
                                                               Button stopButton,
                                                               TableView<EventStatistic> tableView,
                                                               CheckComboBox<String> comboBoxCategories) {
        task = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        service.executeEventCollectionFilteringByCategories(
                                jarInput.getText(),
                                jfrOutput,
                                recordingDuration.getText(),
                                xesOutput.getText() + "/output.xes",
                                comboBoxCategories.getCheckModel().getCheckedItems(),
                                args.getText(),
                                settings,
                                true,
                                false,
                                textField,
                                tableView
                        );
                        return null;
                    }
                };
            }

            @Override
            protected void cancelled() {
                resetUIAfterCancellation(executeButton, stopButton);
                textField.clear();
            }

            @Override
            protected void succeeded() {
                updateUIAfterAction(executeButton, stopButton);
            }
        };

        task.restart();
        stopButton.setDisable(false);
        executeButton.setDisable(true);
    }

    private void handleEventExecutionWhenFilteringByNames(Button executeButton,
                                                          TextField jarInput,
                                                          String jfrOutput,
                                                          TextField recordingDuration,
                                                          TextField xesOutput,
                                                          TextField args,
                                                          String settings,
                                                          TextArea textField,
                                                          Button stopButton,
                                                          TableView<EventStatistic> tableView,
                                                          CheckComboBox<String> comboBoxNames) {
        task = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        service.executeEventCollectionFilteringByNames(
                                jarInput.getText(),
                                jfrOutput,
                                recordingDuration.getText(),
                                xesOutput.getText() + "/output.xes",
                                comboBoxNames.getCheckModel().getCheckedItems(),
                                args.getText(),
                                settings,
                                true,
                                false,
                                textField,
                                tableView
                        );
                        return null;
                    }
                };
            }

            @Override
            protected void cancelled() {
                resetUIAfterCancellation(executeButton, stopButton);
                textField.clear();
            }

            @Override
            protected void succeeded() {
                updateUIAfterAction(executeButton, stopButton);
            }
        };

        task.restart();
        stopButton.setDisable(false);
        executeButton.setDisable(true);
    }

    private void handleNormalEventExecution(Button executeButton,
                                            TextField jarInput,
                                            String jfrOutput,
                                            TextField recordingDuration,
                                            TextField xesOutput,
                                            TextField args,
                                            String settings,
                                            TextArea textField,
                                            Button stopButton,
                                            TableView<EventStatistic> tableView) {
        task = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        service.executeNormalEventCollection(jarInput.getText(),
                                jfrOutput,
                                recordingDuration.getText(),
                                xesOutput.getText() + "/output.xes",
                                args.getText(),
                                settings,
                                true,
                                false,
                                textField,
                                tableView
                        );
                        return null;
                    }
                };
            }

            @Override
            protected void succeeded() {
                updateUIAfterAction(executeButton, stopButton);
            }

            @Override
            protected void cancelled() {
                resetUIAfterCancellation(executeButton, stopButton);
                textField.clear();
            }
        };

        task.restart();
        stopButton.setDisable(false);
        executeButton.setDisable(true);
    }

    public void interruptExecution(Button interruptButton, Button executeButton) {
        interruptButton.setOnAction(e -> {
            task.cancel();
            executeButton.setDisable(false);
            interruptButton.setDisable(true);
        });
    }

    private void updateUIAfterAction(Button startButton, Button stopButton) {
        startButton.setDisable(false);
        stopButton.setDisable(true);
    }

    private void resetUIAfterCancellation(Button startButton, Button stopButton) {
        startButton.setDisable(false);
        stopButton.setDisable(true);
    }

    public void setVisitDocLink(MenuItem menuItem) {
        menuItem.setOnAction(e -> {
            try {
                URI uri = new URI(DOC_LINK);

                service.browse(uri);
            } catch (URISyntaxException | IOException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }
}