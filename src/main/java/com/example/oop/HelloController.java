package com.example.oop;

import com.example.oop.database.DBManager;
import com.example.oop.model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import java.io.File;

public class HelloController {

    @FXML private ListView<Song> songListView;
    @FXML private Label lblCurrentSong, lblCurrentTime, lblTotalTime, listTitle;
    @FXML private VBox albumSection;
    @FXML private Button btnAddSong, btnPlayPause; 
    @FXML private Slider progressSlider;

    private ObservableList<Song> allSongs = FXCollections.observableArrayList();
    private ObservableList<Song> savedSongs = FXCollections.observableArrayList();
    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        DBManager.initDatabase();
        loadSongsFromDatabase();
        setupListView();
        setupProgressSlider();
    }

    private void setupListView() {
        if (songListView != null) {
            setupContextMenu();
            songListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> {
                if (newValue != null) playMusic(newValue);
            });
        }
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem saveItem = new MenuItem("⭐ 加入收藏 (Save)");
        saveItem.setOnAction(e -> {
            Song s = songListView.getSelectionModel().getSelectedItem();
            if (s != null && !savedSongs.contains(s)) savedSongs.add(s);
        });

        MenuItem deleteItem = new MenuItem("🗑 徹底從資料庫移除");
        deleteItem.setStyle("-fx-text-fill: red;");
        deleteItem.setOnAction(e -> {
            Song s = songListView.getSelectionModel().getSelectedItem();
            if (s != null) {
                DBManager.deleteSong(s.getId());
                allSongs.remove(s);
                savedSongs.remove(s);
                if (mediaPlayer != null) { mediaPlayer.stop(); mediaPlayer.dispose(); mediaPlayer = null; }
                lblCurrentSong.setText("已移除歌曲");
            }
        });
        contextMenu.getItems().addAll(saveItem, new SeparatorMenuItem(), deleteItem);
        songListView.setContextMenu(contextMenu);
    }

    private void setupProgressSlider() {
        progressSlider.setOnMousePressed(e -> { if (mediaPlayer != null) mediaPlayer.pause(); });
        progressSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
                mediaPlayer.play();
                btnPlayPause.setText("⏸");
            }
        });
    }

    private void playMusic(Song song) {
        if (mediaPlayer != null) { mediaPlayer.stop(); mediaPlayer.dispose(); }
        try {
            File file = new File(song.getSong_file_path());
            if (file.exists()) {
                mediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
                mediaPlayer.setOnReady(() -> {
                    progressSlider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
                    lblTotalTime.setText(formatTime(mediaPlayer.getMedia().getDuration().toSeconds()));
                });
                mediaPlayer.currentTimeProperty().addListener((obs, old, now) -> {
                    if (!progressSlider.isPressed()) {
                        progressSlider.setValue(now.toSeconds());
                        lblCurrentTime.setText(formatTime(now.toSeconds()));
                    }
                });
                mediaPlayer.play();
                lblCurrentSong.setText("正在播放：" + song.getTitle());
                btnPlayPause.setText("⏸");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String formatTime(double sec) { return String.format("%d:%02d", (int)sec/60, (int)sec%60); }

    @FXML protected void onPlayPauseClick() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) { mediaPlayer.pause(); btnPlayPause.setText("▶"); }
        else { mediaPlayer.play(); btnPlayPause.setText("⏸"); }
    }

    @FXML protected void onHomeClick() { albumSection.setVisible(true); albumSection.setManaged(true); listTitle.setText("🎵 你的單曲 (Singles)"); songListView.setItems(allSongs); }
    @FXML protected void onSaveClick() { albumSection.setVisible(false); albumSection.setManaged(false); listTitle.setText("🎵 你的歌單"); songListView.setItems(savedSongs); }
    
    @FXML protected void onAddSongClick() {
        FileChooser fc = new FileChooser();
        fc.setTitle("選擇音樂檔案");
        // 🌟 關鍵修改：限縮檔案格式為 mp3 與 wav
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("音樂檔案 (mp3, wav)", "*.mp3", "*.wav")
        );
        
        File file = fc.showOpenDialog(btnAddSong.getScene().getWindow());
        if (file != null && DBManager.addSong(file.getName(), "", "", file.getAbsolutePath(), "", 0)) {
            loadSongsFromDatabase();
        }
    }

    private void loadSongsFromDatabase() { allSongs.setAll(DBManager.getAllSongs()); songListView.setItems(allSongs); }
    @FXML protected void onPrevClick() { int i = songListView.getSelectionModel().getSelectedIndex(); if (i > 0) songListView.getSelectionModel().select(i - 1); }
    @FXML protected void onNextClick() { int i = songListView.getSelectionModel().getSelectedIndex(); if (i < allSongs.size() - 1) songListView.getSelectionModel().select(i + 1); }
}