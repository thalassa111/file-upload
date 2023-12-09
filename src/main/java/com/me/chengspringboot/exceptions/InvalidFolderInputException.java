package com.me.chengspringboot.exceptions;

public class InvalidFolderInputException extends RuntimeException{
    public InvalidFolderInputException(String message) {
        super(message);
    }
}
