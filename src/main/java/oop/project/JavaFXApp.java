package oop.project;

import oop.project.gui.DangNhapView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management System");

        // Start with Login View
        DangNhapView loginView = new DangNhapView(primaryStage);
        Scene scene = new Scene(loginView.getView(), 400, 300);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
