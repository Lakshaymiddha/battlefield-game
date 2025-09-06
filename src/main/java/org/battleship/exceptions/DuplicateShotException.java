package org.battleship.exceptions;

public class DuplicateShotException extends GameException {
    public DuplicateShotException(String message) {
        super(message);
    }
}
