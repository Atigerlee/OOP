package com.example.oop.database;
import com.example.oop.model.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBManager {
    private static  final  String URL = "jdbc:sqlite:music.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS songs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL,"+
                "artist TEXT,"+
                "album TEXT,"+
                "song_file_path TEXT NOT NULL,"+
                "photo_file_path TEXT,"+
                "duration INTEGER" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("db init succeed");
        } catch (SQLException e) {
            System.err.println("db init failed " + e.getMessage());
        }
    }
    public static void addSong(String title, String path) {
        String sql = "INSERT INTO songs(title, song_file_path) VALUES(?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static List<Song> getAllSongs() {
        List<Song> songList = new ArrayList<>();
        String sql = "SELECT * FROM songs";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("song_file_path")
                );
                songList.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songList;
    }
}
