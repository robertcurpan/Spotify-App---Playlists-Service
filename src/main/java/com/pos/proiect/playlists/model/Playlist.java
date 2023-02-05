package com.pos.proiect.playlists.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("playlists")
@Getter @Setter @NoArgsConstructor
public class Playlist {

    @Id
    private String playlistId;
    private String playlistName;
    private List<Song> playlistSongs;


    public Playlist(String playlistName, List<Song> playlistSongs) {
        this.playlistName = playlistName;
        this.playlistSongs = playlistSongs;
    }
}