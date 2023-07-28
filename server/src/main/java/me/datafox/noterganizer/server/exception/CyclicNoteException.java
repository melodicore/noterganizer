package me.datafox.noterganizer.server.exception;

/**
 * Thrown when a cyclic dependency would form while moving a note.
 *
 * @author datafox
 */
public class CyclicNoteException extends RuntimeException {
}
