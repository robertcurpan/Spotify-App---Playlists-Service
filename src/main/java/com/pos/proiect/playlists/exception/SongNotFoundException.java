package com.pos.proiect.playlists.exception;


import lombok.Getter;

@Getter
public class SongNotFoundException extends Exception {

    private Integer songId;

    public SongNotFoundException() {
        super();
    }

    public SongNotFoundException(String message) {
        super(message);
    }

    public SongNotFoundException(Integer songId) {
        super();
        this.songId = songId;
    }
}
