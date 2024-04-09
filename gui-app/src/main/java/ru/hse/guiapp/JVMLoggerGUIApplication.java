package ru.hse.guiapp;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.hse.guiapp.view.JVMLoggerView;

import java.util.Objects;

public class JVMLoggerGUIApplication extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("JVM Logger GUI Application");

        JVMLoggerView view = new JVMLoggerView(stage);

        stage.setScene(view.getScene());

        stage.setMinHeight(600);
        stage.setMinWidth(600);
        stage.getIcons().add(new Image(Objects.requireNonNull(JVMLoggerGUIApplication.class.getResourceAsStream("/logo.png"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}