package me.datafox.noterganizer.client.factory;

import me.datafox.noterganizer.client.service.impl.ConnectionServiceImpl;

import java.net.http.HttpRequest;

/**
 * A factory for creating {@link HttpRequest.BodyPublisher} instances to be
 * used with {@link ConnectionServiceImpl}.
 *
 * @author datafox
 */
public interface JsonBodyPublisherFactory {
    /**
     * @param data data to be serialized
     * @return body publisher for the data
     */
    HttpRequest.BodyPublisher buildBodyHandler(Object data);
}
