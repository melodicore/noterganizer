package me.datafox.noterganizer.client.exception;

import me.datafox.noterganizer.client.service.impl.ConnectionServiceImpl;

/**
 * Thrown by {@link ConnectionServiceImpl} if a request to the server is attempted
 * without a successful connection. Should never happen unless the code is fucked.
 *
 * @author datafox
 */
public class NotConnectedException extends RuntimeException {
}
