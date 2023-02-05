package com.pos.proiect.playlists.exception;

public class AuthorizationHeaderMissingException extends Exception {
    public AuthorizationHeaderMissingException() {
        super();
    }

    public AuthorizationHeaderMissingException(String message) {
        super(message);
    }
}
