package com.example.oop;

import com.example.oop.database.DBManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MusicApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 初始化資料庫
        DBManager.initDatabase();
        
        FXMLLoader fxmlLoader = new FXMLLoader(MusicApp.class.getResource("GugaMusic.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Guga Music Player");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}