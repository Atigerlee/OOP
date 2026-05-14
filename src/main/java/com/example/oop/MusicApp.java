package com.example.oop;

import com.example.oop.database.DBManager; //import com.example.oop.database.DBManager; //import DBManager
import javafx.application.Application; // import javafx.fxml.FXML; //import javafx.fxml.FXML
import javafx.fxml.FXMLLoader; //import javafx.scene.Parent; //import Parent    
import javafx.scene.Scene; 
import javafx.stage.Stage; 

import java.io.IOException;

public class MusicApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DBManager.initDatabase();
        FXMLLoader fxmlLoader = new FXMLLoader(MusicApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("Music Player");
        stage.setScene(scene);
        stage.show();
    }
}
