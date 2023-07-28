package me.datafox.noterganizer.server.exception;

/**
 * Thrown when the root note is attempted to be removed without removing
 * its associated space with it.
 *
 * @author datafox
 */
public class RemoveRootNoteException extends RuntimeException {
}
