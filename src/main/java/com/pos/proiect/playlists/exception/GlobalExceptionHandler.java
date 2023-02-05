package com.pos.proiect.playlists.exception;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { UserNotFoundException.class })
    public ResponseEntity<ErrorObject> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        String errorMessage = "The song with id " + ex.getUserId() + " was not found!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.NOT_FOUND, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { PlaylistNotFoundException.class })
    public ResponseEntity<ErrorObject> handlePlaylistNotFoundException(PlaylistNotFoundException ex, WebRequest request) {
        String errorMessage = "The playlist with id " + ex.getPlaylistId() + " was not found!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.NOT_FOUND, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { SongNotFoundException.class })
    public ResponseEntity<ErrorObject> handleSongNotFoundException(SongNotFoundException ex, WebRequest request) {
        String errorMessage = "The song with id " + ex.getSongId() + " was not found!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.NOT_FOUND, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { UserProfileAlreadyExistsException.class })
    public ResponseEntity<ErrorObject> handleUserProfileAlreadyExistsException(UserProfileAlreadyExistsException ex, WebRequest request) {
        String errorMessage = "The user with id " + ex.getUserId() + " already exists!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.CONFLICT, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = { UnknownException.class })
    public ResponseEntity<ErrorObject> handleUnknownException(UnknownException ex, WebRequest request) {
        String errorMessage = "There was an error when trying to communicate with the SongCollection REST API!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { JsonProcessingException.class })
    public ResponseEntity<ErrorObject> handleJsonProcessingException(JsonProcessingException ex, WebRequest request) {
        String errorMessage = "There was an error while processing a JSON!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
        //TODO http status??
    }

    @ExceptionHandler(value = { AccessForbiddenException.class })
    public ResponseEntity<ErrorObject> handleAccessForbiddenException(AccessForbiddenException ex, WebRequest request) {
        String errorMessage = "You don't have the proper role for this operation!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.UNAUTHORIZED, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { JwsSignatureNotValidException.class })
    public ResponseEntity<ErrorObject> handleJwsSignatureNotValidException(JwsSignatureNotValidException ex, WebRequest request) {
        String errorMessage = "Jws signature is not valid!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.UNAUTHORIZED, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { JwsTokenCouldNotBeValidatedException.class })
    public ResponseEntity<ErrorObject> handleJwsTokenCouldNotBeValidatedException(JwsTokenCouldNotBeValidatedException ex, WebRequest request) {
        String errorMessage = "Jws token could not be validated!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.UNAUTHORIZED, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { JwsFormatNotValidException.class })
    public ResponseEntity<ErrorObject> handleJwsFormatNotValidException(JwsFormatNotValidException ex, WebRequest request) {
        String errorMessage = "Jws format is not valid!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.UNAUTHORIZED, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { JwsExpiredException.class })
    public ResponseEntity<ErrorObject> handleJwsExpiredException(JwsExpiredException ex, WebRequest request) {
        String errorMessage = "Jws expired!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.UNAUTHORIZED, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { AuthorizationHeaderMissingException.class })
    public ResponseEntity<ErrorObject> handleAuthorizationHeaderMissingException(AuthorizationHeaderMissingException ex, WebRequest request) {
        String errorMessage = "Missing authorization header!";
        ErrorObject errorObject = new ErrorObject(errorMessage, HttpStatus.UNAUTHORIZED, LocalDateTime.now());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.UNAUTHORIZED);
    }
}
