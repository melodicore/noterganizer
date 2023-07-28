package me.datafox.noterganizer.client.service.impl;

import me.datafox.noterganizer.api.Constants;
import me.datafox.noterganizer.client.exception.EmptyAddressException;
import me.datafox.noterganizer.client.exception.NotConnectedException;
import me.datafox.noterganizer.client.factory.JsonBodyHandlerFactory;
import me.datafox.noterganizer.client.factory.JsonBodyPublisherFactory;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.Context;
import me.datafox.noterganizer.client.model.Response;
import me.datafox.noterganizer.client.service.ConnectionService;
import org.apache.logging.log4j.Logger;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Connection service implementation.
 *
 * @author datafox
 */
@Component
public class ConnectionServiceImpl implements ConnectionService {
    private final HttpClient client;

    private final Context context;

    private final Logger logger;

    private final JsonBodyHandlerFactory jsonBodyHandlerFactory;

    private final JsonBodyPublisherFactory jsonBodyPublisherFactory;

    @Inject
    public ConnectionServiceImpl(HttpClient client,
                                 Context context,
                                 Logger logger,
                                 JsonBodyHandlerFactory jsonBodyHandlerFactory,
                                 JsonBodyPublisherFactory jsonBodyPublisherFactory) {
        this.client = client;
        this.context = context;
        this.logger = logger;
        this.jsonBodyHandlerFactory = jsonBodyHandlerFactory;
        this.jsonBodyPublisherFactory = jsonBodyPublisherFactory;
    }

    @Override
    public Response<String> connect(String address) {
        logger.info("Parsing address " + address);
        try {
            address = parseAddress(address);
        } catch(EmptyAddressException e) {
            logger.info("Address is empty", e);
            return Response.error(e);
        }
        HttpRequest request = HttpRequest.newBuilder(URI.create(address + "version")).GET().build();

        Response<String> response = sendRequest(request, this::stringHandler);

        if(response.isError()) {
            return Response.error(response.status());
        }

        String version = response.get();

        if(Constants.IDENTIFIER.equals(version)) {
            logger.info("Connection to " + request.uri() + " established");
            context.setAddress(address);
            return Response.of("success");
        }
        String error = "API version mismatch! Client: " + Constants.API_VERSION + ", Server: " + version.replaceAll("[^0-9]", "");
        logger.error(error);
        return Response.error(error);
    }

    @Override
    public Response<String> login(String username, String password, boolean remember) {
        logger.info("Logging in with username " + username);

        if(context.getAddress() == null) {
            logger.error("Not connected! This should never happen");
            throw new NotConnectedException();
        }

        byte[] data = ("username=" + username + "&password=" + password + "&remember=" + remember).getBytes();

        HttpRequest loginRequest = HttpRequest.newBuilder(URI.create(context.getAddress() + "login"))
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Response<String> response = sendRequest(loginRequest, this::stringHandler);
        logger.info("Login successful");
        return response;
    }

    @Override
    public <T> Response<T> get(String url, Class<T> responseType) {
        if(context.getAddress() == null) throw new NotConnectedException();
        HttpRequest request = HttpRequest.newBuilder(URI.create(context.getAddress() + url)).GET().build();
        return sendRequest(request, jsonBodyHandlerFactory.buildBodyHandler(responseType));
    }

    @Override
    public <T> Response<T> post(Object data, String url, Class<T> responseType) {
        if(context.getAddress() == null) throw new NotConnectedException();
        HttpRequest request = HttpRequest.newBuilder(URI.create(context.getAddress() + url))
                .POST(jsonBodyPublisherFactory.buildBodyHandler(data))
                .header("Content-Type", "application/json")
                .build();
        return sendRequest(request, jsonBodyHandlerFactory.buildBodyHandler(responseType));
    }

    public <T> Response<T> delete(String url, Class<T> responseType) {
        if(context.getAddress() == null) throw new NotConnectedException();
        HttpRequest request = HttpRequest.newBuilder(URI.create(context.getAddress() + url)).DELETE().build();
        return sendRequest(request, jsonBodyHandlerFactory.buildBodyHandler(responseType));
    }

    @Override
    public Optional<HttpCookie> getRememberCookie() {
        Optional<CookieManager> optional = client.cookieHandler().flatMap(handler -> {
            if(handler instanceof CookieManager manager) return Optional.of(manager);
            return Optional.empty();
        });

        if(optional.isEmpty()) return Optional.empty();

        CookieManager manager = optional.get();

        return manager.getCookieStore()
                .get(URI.create(context.getAddress()))
                .stream()
                .filter(cookie -> cookie.getName().equals("REMEMBER"))
                .findAny();
    }

    @Override
    public void setRememberCookie(HttpCookie cookie) {
        Optional<CookieManager> optional = client.cookieHandler().flatMap(handler -> {
            if(handler instanceof CookieManager manager) return Optional.of(manager);
            return Optional.empty();
        });

        if(optional.isEmpty()) return;

        CookieManager manager = optional.get();

        if(cookie == null) {
            getRememberCookie().ifPresent(cookie1 ->
                    manager.getCookieStore().remove(URI.create(context.getAddress()), cookie1));
        } else {
            manager.getCookieStore().add(URI.create(context.getAddress()), cookie);
        }
    }

    private <T> Response<T> sendRequest(HttpRequest request, HttpResponse.BodyHandler<Response<T>> handler) {
        logger.info("Sending " + request.method() + " request to " + request.uri());

        HttpResponse<Response<T>> response;
        try {
            response = client.send(request, handler);
        } catch(Exception e) {
            logger.error("Request failed", e);
            return Response.error(e);
        }
        return response.body();
    }

    private String parseAddress(String address) throws EmptyAddressException {
        if(address.isBlank()) throw new EmptyAddressException();
        if(!address.startsWith("http://") ||
                !address.startsWith("https://")) {
            address = "https://" + address;
        }
        if(!address.endsWith("/")) {
            address = address + "/";
        }
        return address;
    }

    private HttpResponse.BodySubscriber<Response<String>> stringHandler(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<String> upstream =
                HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);

        if(responseInfo.statusCode() == 200 || responseInfo.statusCode() == 302) {
            return HttpResponse.BodySubscribers.mapping(upstream, Response::of);
        }

        return HttpResponse.BodySubscribers.mapping(upstream, Response::error);
    }
}
