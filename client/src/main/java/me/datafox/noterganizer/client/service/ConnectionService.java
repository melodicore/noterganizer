package me.datafox.noterganizer.client.service;

import me.datafox.noterganizer.client.model.Response;

import java.net.HttpCookie;
import java.util.Optional;

/**
 * The connection service handles the lower level network requests.
 *
 * @author datafox
 */
public interface ConnectionService {
    /**
     * @param address address to connect to
     * @return response containing either "success" or an error status
     */
    Response<String> connect(String address);

    /**
     * @param username username
     * @param password password
     * @param remember if set to true, remember me will be enabled
     * @return response containing either "success" or an error status
     */
    Response<String> login(String username, String password, boolean remember);

    /**
     * @param url URL to send the GET request to
     * @param responseType Class denoting the type to be requested
     * @return response containing either the requested data or an error status
     */
    <T> Response<T> get(String url, Class<T> responseType);

    /**
     * @param data request body
     * @param url URL to send the POST request to
     * @param responseType Class denoting the type to be returned
     * @return response containing either the returned data or an error status
     */
    <T> Response<T> post(Object data, String url, Class<T> responseType);

    /**
     * @param url URL to send the DELETE request to
     * @param responseType Class denoting the type to be returned
     * @return response containing either the returned data or an error status
     */
    <T> Response<T> delete(String url, Class<T> responseType);

    /**
     * @return remember me HTTP cookie, if one is present
     */
    Optional<HttpCookie> getRememberCookie();

    /**
     * @param cookie remember me HTTP cookie to be used. May be null
     */
    void setRememberCookie(HttpCookie cookie);
}
