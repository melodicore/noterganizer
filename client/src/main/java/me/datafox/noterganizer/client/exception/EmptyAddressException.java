package me.datafox.noterganizer.client.exception;

import me.datafox.noterganizer.client.service.impl.ConnectionServiceImpl;

/**
 * Thrown by {@link ConnectionServiceImpl} when trying to connect to an empty address.
 * Handled internally.
 *
 * @author datafox
 */
public class EmptyAddressException extends Exception {
}
