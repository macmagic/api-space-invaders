package com.juanarroyes.apispaceinvaders.exception;

public class SavegameNotFoundException extends AppException {

    public SavegameNotFoundException () {

    }

    public SavegameNotFoundException (String message) {
        super(message);
    }

    public SavegameNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }

    public SavegameNotFoundException (Throwable cause) {
        super(cause);
    }

}
