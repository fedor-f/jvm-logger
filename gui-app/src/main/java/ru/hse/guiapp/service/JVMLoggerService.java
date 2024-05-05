package ru.hse.guiapp.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.hse.api.CommandExecutor;
import ru.hse.guiapp.config.EventInfo;
import ru.hse.guiapp.model.EventStatistic;
import ru.hse.guiapp.util.JoinMapUtil;
import ru.hse.util.DurationUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                                             boolean showStatistics,
                                             boolean verbose,
                                             TextArea textField, TableView<EventStatistic> tableView) {
        tableView.getItems().clear();
        textField.clear();
        textField.setText("\uD83D\uDD04 Executing .jar...");

        var optMap = CommandExecutor.normalEventCollection(jarInput, jfrOutput, recordingDuration, xesOutput,
                args, showStatistics, verbose);

        textField.appendText("\n✅ Events collected successfully");

        if (optMap.isPresent()) {
            var stringIntegerMap = optMap.get();
            fillTable(tableView, stringIntegerMap);
        }
    }

    public void executeEventCollectionFilteringByNames(String jarInput,
                                                       String jfrOutput,
                                                       String recordingDuration,
                                                       String xesOutput,
                                                       List<String> names,
                                                       String args,
                                                       boolean showStatistics,
                                                       boolean verbose,
                                                       TextArea textField,
                                                       TableView<EventStatistic> tableView) {
        tableView.getItems().clear();
        textField.clear();
        textField.setText("\uD83D\uDD04 Executing .jar...");

        var optMap = CommandExecutor.filteredByNamesEventCollection(jarInput,
                jfrOutput,
                recordingDuration,
                xesOutput,
                names,
                args,
                showStatistics,
                verbose
        );

        textField.appendText("\n✅ Events collected successfully");

        if (optMap.isPresent()) {
            var stringIntegerMap = optMap.get();
            fillTable(tableView, stringIntegerMap);
        }
    }

    public void executeEventCollectionFilteringByCategories(String jarInput,
                                                            String jfrOutput,
                                                            String recordingDuration,
                                                            String xesOutput,
                                                            List<String> categories,
                                                            String args,
                                                            boolean showStatistics,
                                                            boolean verbose,
                                                            TextArea textField,
                                                            TableView<EventStatistic> tableView) {
        tableView.getItems().clear();
        textField.clear();
        textField.setText("\uD83D\uDD04 Executing .jar...");

        var optMap = CommandExecutor.filteredByCategoriesEventCollection(
                jarInput,
                jfrOutput,
                recordingDuration,
                xesOutput,
                categories,
                args,
                showStatistics,
                verbose
        );

        textField.appendText("\n✅ Events collected successfully");

        if (optMap.isPresent()) {
            var stringIntegerMap = optMap.get();
            fillTable(tableView, stringIntegerMap);
        }
    }

    private void fillTable(TableView<EventStatistic> tableView, Map<String, Integer> stringIntegerMap) {
        var map = JoinMapUtil.innerJoin(EventInfo.EVENT_NAME_MAP, stringIntegerMap);

        List<EventStatistic> list = new ArrayList<>();
        map.forEach((key, value) -> list.add(new EventStatistic(key, value.first(), value.second())));

        ObservableList<EventStatistic> observableList = FXCollections.observableList(list);

        tableView.getItems().addAll(observableList);
    }

    public void browse(URI uri) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();

            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Browse");
                alert.setContentText("Browse action is not supported");

                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Desktop");
            alert.setContentText("Desktop is not supported");

            alert.showAndWait();
        }
    }
}
