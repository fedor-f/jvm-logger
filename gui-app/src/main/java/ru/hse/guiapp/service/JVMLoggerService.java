package ru.hse.guiapp.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.hse.api.CommandExecutor;
import ru.hse.guiapp.config.EventInfo;
import ru.hse.guiapp.model.EventStatistic;
import ru.hse.guiapp.util.JoinMapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
                                             TextArea textField, TableView<EventStatistic> tableView) {
        tableView.getItems().clear();
        textField.clear();
        textField.setText("Executing .jar...");

        var optMap = CommandExecutor.normalEventCollection(jarInput, jfrOutput, recordingDuration, xesOutput,
                args, settings, showStatistics, verbose);

        textField.appendText("\nEvents collected successfully\n");

        if (optMap.isPresent()) {
            var stringIntegerMap = optMap.get();
            var map = JoinMapUtil.innerJoin(EventInfo.EVENT_NAME_MAP, stringIntegerMap);

            List<EventStatistic> list = new ArrayList<>();
            map.forEach((key, value) -> {
                list.add(new EventStatistic(key, value.first(), value.second()));
            });

            ObservableList<EventStatistic> observableList = FXCollections.observableList(list);

            tableView.getItems().addAll(observableList);
        }
    }
}
