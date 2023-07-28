package me.datafox.noterganizer.client.event;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple event handler.
 *
 * @author datafox
 */
public class EventSubscription {
    private final Set<Runnable> listeners;

    public EventSubscription() {
        listeners = new HashSet<>();
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    public void call() {
        listeners.forEach(Runnable::run);
    }
}
