package me.datafox.noterganizer.client.exception;

import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.injection.Injector;

/**
 * Thrown by {@link Injector} when a class has multiple constructors present
 * annotated with {@link Inject}, if a constructor annotated with it has
 * parameters unknown to {@link Injector}, or if no such constructor and no
 * default constructor are present.
 *
 * @author datafox
 */
public class ConstructorValidityException extends RuntimeException {
    public ConstructorValidityException(String message) {
        super(message);
    }

    public ConstructorValidityException(Throwable cause) {
        super(cause);
    }
}
