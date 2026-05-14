package com.example.oop.model;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String album;
    private String song_file_path;
    private String photo_file_path;
    private int duration;

    public Song(int id, String title, String artist, String album,String song_file_path) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.song_file_path = song_file_path;
        this.photo_file_path = photo_file_path;
        this.duration = duration;
    }

    // accessor
    public int getId(){ return id;}
    public String getTitle(){ return title;}
    public String getArtist(){ return artist;}
    public String getAlbum(){ return album;}
    public String getSong_file_path(){ return song_file_path;}
    public String getPhoto_file_path(){ return photo_file_path;}
    public int getDuration(){ return duration;}

    @Override
    public String toString(){
        return title+" - "+artist;
    }
}
