package com.example.oop;

import com.example.oop.database.DBManager;
import com.example.oop.model.Song;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HelloController {

    @FXML
    private ListView<Song> songListView;
    
    @FXML
    private Label lblCurrentSong;

    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        refreshSongList();
        
        songListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                playSong(newValue);
            }
        });
    }

    private void refreshSongList() {
        songListView.getItems().setAll(DBManager.getAllSongs());
    }

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
            DBManager.addSong(title, path);
            System.out.println("add: " + title);
            refreshSongList();
        }
    }

    private void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        lblCurrentSong.setText("Playing: " + song.getTitle());
        
        try {
            File file = new File(song.getSong_file_path());
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing media: " + e.getMessage());
            lblCurrentSong.setText("Error playing: " + song.getTitle());
        }
    }

    @FXML
    protected void onPlayClick() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        } else {
            Song selected = songListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                playSong(selected);
            }
        }
    }

    @FXML
    protected void onPauseClick() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @FXML
    protected void onStopClick() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
