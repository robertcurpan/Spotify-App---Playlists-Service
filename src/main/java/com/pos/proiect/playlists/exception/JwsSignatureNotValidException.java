package com.pos.proiect.playlists.exception;

public class JwsSignatureNotValidException extends Exception {
    public JwsSignatureNotValidException() {
        super();
    }

    public JwsSignatureNotValidException(String message) {
        super(message);
    }
}
