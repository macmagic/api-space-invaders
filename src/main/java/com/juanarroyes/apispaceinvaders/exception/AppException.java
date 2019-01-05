package com.juanarroyes.apispaceinvaders.exception;

class AppException extends Exception {

    AppException () {
        super();
    }

    AppException (String message) {
        super(message);
    }

    AppException (String message, Throwable cause) {
        super(message, cause);
    }

    AppException (Throwable cause) {
        super(cause);
    }

}
