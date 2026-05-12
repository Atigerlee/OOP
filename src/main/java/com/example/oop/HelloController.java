package com.example.oop;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.example.oop.database.DBManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onAddSongClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("音樂檔案", "*.mp3", "*.wav")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            String title = selectedFile.getName();
            String path = selectedFile.getPath();
            saveSongToDatabase(title, path);
            System.out.println("add: " + title);
        }
    }
    private void saveSongToDatabase(String title, String path) {
        DBManager.addSong(title, path);
    }
    @FXML
    private ListView<String> songListView;
}
