package me.datafox.noterganizer.client.exception;

import me.datafox.noterganizer.client.injection.Injector;

/**
 * Thrown by {@link Injector} when class instantiation is not successful for an unexpected reason.
 *
 * @author datafox
 */
public class InjectorInstantiationException extends RuntimeException {
    public InjectorInstantiationException(Throwable cause) {
        super(cause);
    }
}
