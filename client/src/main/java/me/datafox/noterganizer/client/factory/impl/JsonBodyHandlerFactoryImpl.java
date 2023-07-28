package me.datafox.noterganizer.client.factory.impl;

import com.google.gson.Gson;
import me.datafox.noterganizer.client.factory.JsonBodyHandlerFactory;
import me.datafox.noterganizer.client.factory.JsonBodyPublisherFactory;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link JsonBodyPublisherFactory}. {@link HttpResponse.BodyHandler}s
 * are cached by type. Uses {@link Gson} to deserialize JSON data.
 *
 * @author datafox
 */

@Component
public class JsonBodyHandlerFactoryImpl implements JsonBodyHandlerFactory {
    private final Gson gson;

    private final Logger logger;

    private final Map<Class<?>, Handler<?>> handlerMap;

    @Inject
    public JsonBodyHandlerFactoryImpl(Gson gson,
                                      Logger logger) {
        this.gson = gson;
        this.logger = logger;

        handlerMap = new HashMap<>();
    }

    /**
     * Check if a {@link HttpResponse.BodyHandler} is already cached and return it,
     * otherwise instantiate a new one.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> HttpResponse.BodyHandler<Response<T>> buildBodyHandler(Class<T> type) {
        logger.info("Requesting body handler for class " + type.getName());

        if(!handlerMap.containsKey(type)) {
            logger.info("No handler found for class " + type.getName() + ", instantiating");
            handlerMap.put(type, new Handler<>(type));
        }

        return (Handler<T>) handlerMap.get(type);
    }

    /**
     * Custom {@link HttpResponse.BodyHandler} implementation using {@link Gson}.
     */
    private class Handler<T> implements HttpResponse.BodyHandler<Response<T>> {
        private final Logger logger;

        private final Class<T> type;

        public Handler(Class<T> type) {
            logger = LogManager.getLogger(getClass());
            this.type = type;
        }

        @Override
        public HttpResponse.BodySubscriber<Response<T>> apply(HttpResponse.ResponseInfo responseInfo) {
            logger.info("Response received");

            HttpResponse.BodySubscriber<String> upstream =
                    HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);

            if(responseInfo.statusCode() == 200 || responseInfo.statusCode() == 302) {
                logger.info("Request was successful");

                return HttpResponse.BodySubscribers.mapping(upstream, this::fromJson);
            }

            logger.info("Request was not successful, error code " + responseInfo.statusCode());

            return HttpResponse.BodySubscribers.mapping(upstream, Response::error);
        }

        private Response<T> fromJson(String string) {
            logger.info("Deserializing response from JSON");

            return Response.of(gson.fromJson(string, type));
        }
    }
}
