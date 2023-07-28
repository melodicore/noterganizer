package me.datafox.noterganizer.client.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.injection.Inject;
import me.datafox.noterganizer.client.model.WindowSettings;
import me.datafox.noterganizer.client.service.SettingsService;
import org.apache.logging.log4j.Logger;

import java.net.HttpCookie;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Settings service implementation.
 *
 * @author datafox
 */
@Component
public class SettingsServiceImpl implements SettingsService {
    private static final String SERVER_ADDRESS = "address";
    private static final String AUTO_CONNECT = "autoConnect";
    private static final String REMEMBER_ME_COOKIE = "remember";
    private static final String WINDOW_SETTINGS = "window";

    private final Logger logger;

    private final Preferences preferences;

    private final Gson gson;

    @Inject
    public SettingsServiceImpl(Logger logger,
                               Preferences preferences,
                               Gson gson) {
        this.logger = logger;
        this.preferences = preferences;
        this.gson = gson;
    }

    @Override
    public String getAddress() {
        logger.info("Fetching server address from preferences");

        return preferences.get(SERVER_ADDRESS, "");
    }

    @Override
    public void setAddress(String address) {
        logger.info("Saving server address to preferences (" + address + ")");

        preferences.put(SERVER_ADDRESS, address);
        flush();
    }

    @Override
    public boolean getAutoConnect() {
        logger.info("Fetching auto connect from preferences");

        return preferences.getBoolean(AUTO_CONNECT, false);
    }

    @Override
    public void setAutoConnect(boolean autoConnect) {
        logger.info("Saving auto connect to preferences (" + autoConnect + ")");

        preferences.putBoolean(AUTO_CONNECT, autoConnect);
        flush();
    }

    @Override
    public HttpCookie getCookie() {
        logger.info("Fetching remember me cookie from preferences");

        try {
            return gson.fromJson(preferences.get(REMEMBER_ME_COOKIE, ""), HttpCookie.class);
        } catch(JsonSyntaxException e) {
            logger.warn("Invalid json data in remember me cookie, removing from preferences", e);

            preferences.remove(REMEMBER_ME_COOKIE);
            flush();
            return null;
        }
    }

    @Override
    public void setCookie(HttpCookie cookie) {
        logger.info("Saving remember me cookie to preferences (" + cookie + ")");

        preferences.put(REMEMBER_ME_COOKIE, gson.toJson(cookie));
        flush();
    }

    @Override
    public void removeCookie() {
        logger.info("Removing remember me cookie from preferences");

        preferences.remove(REMEMBER_ME_COOKIE);
        flush();
    }

    @Override
    public WindowSettings getWindowSettings() {
        logger.info("Fetching window position from preferences");

        try {
            return gson.fromJson(preferences.get(WINDOW_SETTINGS, ""), WindowSettings.class);
        } catch(JsonSyntaxException e) {
            logger.warn("Invalid json data in window position, removing from preferences", e);

            preferences.remove(WINDOW_SETTINGS);
            flush();
            return null;
        }
    }

    @Override
    public void setWindowSettings(WindowSettings windowSettings) {
        logger.info("Saving remember window settings to preferences (" + windowSettings + ")");

        preferences.put(WINDOW_SETTINGS, gson.toJson(windowSettings));
        flush();
    }

    private void flush() {
        logger.debug("Flushing preferences");
        try {
            preferences.flush();
        } catch(BackingStoreException e) {
            logger.error("Could not flush preferences", e);
        }
    }
}
