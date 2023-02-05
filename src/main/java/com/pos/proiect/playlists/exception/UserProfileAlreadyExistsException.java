package com.pos.proiect.playlists.exception;


import lombok.Getter;

@Getter
public class UserProfileAlreadyExistsException extends Exception {

    private Integer userId;

    public UserProfileAlreadyExistsException() {
        super();
    }

    public UserProfileAlreadyExistsException(String message) {
        super(message);
    }

    public UserProfileAlreadyExistsException(Integer userId) {
        super();
        this.userId = userId;
    }
}
