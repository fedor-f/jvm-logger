package ru.hse.guiapp;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.hse.guiapp.view.JVMLoggerView;

public class JVMLoggerGUIApplication extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("JVM Logger GUI Application");

        JVMLoggerView view = new JVMLoggerView(stage);

        stage.setScene(view.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}