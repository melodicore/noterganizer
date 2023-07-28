package me.datafox.noterganizer.client.exception;

import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Injector;

/**
 * Thrown by {@link Injector} when there is a cyclic dependency between classes
 * annotated with {@link Component}.
 *
 * @author datafox
 */
public class CyclicDependencyException extends RuntimeException {
    public CyclicDependencyException(String message) {
        super(message);
    }
}
