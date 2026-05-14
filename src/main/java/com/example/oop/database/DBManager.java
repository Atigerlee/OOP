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

        boolean shouldInsert = false;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("db init succeed");
            
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM songs")) {
                if (rs.next() && rs.getInt(1) < 3) {
                    shouldInsert = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("db init failed " + e.getMessage());
        }

        if (shouldInsert) {
            System.out.println("Inserting mock data...");
            addSong("Jay Chou - 七里香 (Qi Li Xiang).mp3", "dummy/path1.mp3");
            addSong("Jay Chou - 夜曲 (Nocturne).mp3", "dummy/path2.mp3");
            addSong("Ed Sheeran - Shape of You", "dummy/path3.mp3");
            addSong("Taylor Swift - Love Story", "dummy/path4.mp3");
            addSong("Coldplay - Yellow", "dummy/path5.mp3");
            addSong("周杰倫 - 晴天 (Sunny Day)", "dummy/path6.mp3");
            addSong("五月天 Mayday - 突然好想你", "dummy/path7.mp3");
            addSong("Eminem - Without Me", "dummy/path8.mp3");
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
        String kw = keyword.toLowerCase();
        org.apache.commons.text.similarity.FuzzyScore fuzzyScore = new org.apache.commons.text.similarity.FuzzyScore(java.util.Locale.ENGLISH);

        return allSongs.stream()
                .map(s -> {
                    String target = s.getTitle().toLowerCase();
                    int score = 0;
                    // 如果直接包含字串，給予極高分數
                    if (target.contains(kw)) {
                        score += 1000; 
                    }
                    // 加上 Apache Commons 的 FuzzyScore (類似 IDE 的搜尋，會抓分散的字母)
                    score += fuzzyScore.fuzzyScore(target, kw);
                    return new java.util.AbstractMap.SimpleEntry<>(s, score);
                })
                .filter(entry -> entry.getValue() > 0) // 過濾掉完全沒有對中任何字母的歌曲
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())) // 分數高的排前面
                .limit(limit)
                .map(java.util.Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
    }
}
