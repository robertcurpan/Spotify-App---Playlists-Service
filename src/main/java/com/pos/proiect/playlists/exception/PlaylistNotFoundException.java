package com.pos.proiect.playlists.exception;


import lombok.Getter;

@Getter
public class PlaylistNotFoundException extends Exception {

    private String playlistId;

    public PlaylistNotFoundException() {
        super();
    }

    public PlaylistNotFoundException(String playlistId) {
        super();
        this.playlistId = playlistId;
    }

}
