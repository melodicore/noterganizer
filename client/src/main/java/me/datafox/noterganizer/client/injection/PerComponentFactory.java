package me.datafox.noterganizer.client.injection;

import java.util.function.Function;

/**
 * The factory interface to be used with component-specific factories
 * with the {@link Injector}.
 *
 * @author datafox
 */
public interface PerComponentFactory<T> extends Function<Class<?>, T> {
    Class<T> getType();
}
