package com.juanarroyes.apispaceinvaders.exception;

public class SaveGameNotFoundException extends AppException {

    public SaveGameNotFoundException() {

    }

    public SaveGameNotFoundException(String message) {
        super(message);
    }

    public SaveGameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveGameNotFoundException(Throwable cause) {
        super(cause);
    }

}
