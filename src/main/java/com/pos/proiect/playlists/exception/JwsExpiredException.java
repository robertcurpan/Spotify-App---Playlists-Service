package com.pos.proiect.playlists.exception;

public class JwsExpiredException extends Exception {
    public JwsExpiredException() {
        super();
    }

    public JwsExpiredException(String message) {
        super(message);
    }
}
