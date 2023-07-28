package me.datafox.noterganizer.client.factory.impl;

import com.google.gson.Gson;
import me.datafox.noterganizer.client.factory.JsonBodyPublisherFactory;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;

import java.net.http.HttpRequest;

/**
 * Implementation of {@link JsonBodyPublisherFactory}. Uses {@link Gson}
 * to serialize JSON data.
 *
 * @author datafox
 */
@Component
public class JsonBodyPublisherFactoryImpl implements JsonBodyPublisherFactory {
    private final Gson gson;

    @Inject
    public JsonBodyPublisherFactoryImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public HttpRequest.BodyPublisher buildBodyHandler(Object data) {
        return HttpRequest.BodyPublishers.ofString(gson.toJson(data));
    }
}
