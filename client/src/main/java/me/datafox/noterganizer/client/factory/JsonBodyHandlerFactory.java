package me.datafox.noterganizer.client.factory;

import me.datafox.noterganizer.client.model.Response;
import me.datafox.noterganizer.client.service.impl.ConnectionServiceImpl;

import java.net.http.HttpResponse;

/**
 * A factory for creating {@link HttpResponse.BodyHandler} instances to be
 * used with {@link ConnectionServiceImpl}.
 *
 * @author datafox
 */
public interface JsonBodyHandlerFactory {
    /**
     * @param type type to be deserialized
     * @return body handler for the type
     */
    <T> HttpResponse.BodyHandler<Response<T>> buildBodyHandler(Class<T> type);
}
