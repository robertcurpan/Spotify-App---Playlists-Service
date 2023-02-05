package com.pos.proiect.playlists.exception;

import lombok.Getter;


@Getter
public class UserNotFoundException extends Exception {

    private Integer userId;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Integer userId) {
        super();
        this.userId = userId;
    }
}
