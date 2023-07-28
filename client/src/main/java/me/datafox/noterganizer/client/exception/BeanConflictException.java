package me.datafox.noterganizer.client.exception;

import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Injector;

/**
 * Thrown by {@link Injector} when a bean is not found during {@link Component}
 * instantiation. Should never happen since in this situation checks preceding
 * the instantiation would throw {@link ConstructorValidityException} instead.
 *
 * @author datafox
 */
public class BeanConflictException extends RuntimeException {
    public BeanConflictException(String message) {
        super(message);
    }
}
