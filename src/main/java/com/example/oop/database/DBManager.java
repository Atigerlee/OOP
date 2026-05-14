package com.example.oop.database;

import com.example.oop.model.Song;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final String URL = "jdbc:sqlite:music.db";

    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS songs (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " title TEXT NOT NULL,\n"
                + " artist TEXT,\n"
                + " album TEXT,\n"
                + " song_file_path TEXT NOT NULL UNIQUE,\n"
                + " photo_file_path TEXT,\n"
                + " duration INTEGER\n"
                + ");";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("資料庫初始化失敗: " + e.getMessage());
        }
    }

    public static boolean addSong(String title, String artist, String album, String songFilePath, String photoFilePath, int duration) {
        String sql = "INSERT INTO songs(title, artist, album, song_file_path, photo_file_path, duration) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            pstmt.setString(3, album);
            pstmt.setString(4, songFilePath);
            pstmt.setString(5, photoFilePath);
            pstmt.setInt(6, duration);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("新增歌曲失敗: " + e.getMessage());
            return false;
        }
    }

    public static void deleteSong(int id) {
        String sql = "DELETE FROM songs WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("刪除資料失敗: " + e.getMessage());
        }
    }

    public static List<Song> getAllSongs() {
        List<Song> songList = new ArrayList<>();
        String sql = "SELECT * FROM songs";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                songList.add(new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("song_file_path"),
                        rs.getString("photo_file_path"),
                        rs.getInt("duration")
                ));
            }
        } catch (SQLException e) {
            System.out.println("讀取歌曲失敗: " + e.getMessage());
        }
        return songList;
    }
}