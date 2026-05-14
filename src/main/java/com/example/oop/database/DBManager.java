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

    public static List<Song> searchSongsFuzzy(String keyword, int limit) {
        List<Song> allSongs = getAllSongs();
        org.apache.commons.text.similarity.JaroWinklerDistance distance = new org.apache.commons.text.similarity.JaroWinklerDistance();

        return allSongs.stream()
                .sorted((s1, s2) -> {
                    double score1 = distance.apply(s1.getTitle().toLowerCase(), keyword.toLowerCase());
                    double score2 = distance.apply(s2.getTitle().toLowerCase(), keyword.toLowerCase());
                    return Double.compare(score2, score1); // Descending order (higher is better)
                })
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }
}
