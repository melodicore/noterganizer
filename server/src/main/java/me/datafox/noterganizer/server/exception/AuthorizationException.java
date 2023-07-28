package me.datafox.noterganizer.server.exception;

/**
 * Thrown when an incorrect password is provided outside of logging in,
 * e.g. while changing their password.
 *
 * @author datafox
 */
public class AuthorizationException extends RuntimeException {
}
