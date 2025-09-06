package org.battleship.exceptions;

/** Base checked exception for user-caused game errors (checked). */
public class GameException extends Exception {
    public GameException(String message) { super(message); }
}
