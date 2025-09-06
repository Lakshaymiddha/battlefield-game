package org.battleship.exceptions;


public class DuplicateFireException extends GameException {
    public DuplicateFireException(String message) {
        super(message);
    }
}