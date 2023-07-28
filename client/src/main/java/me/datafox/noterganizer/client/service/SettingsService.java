package me.datafox.noterganizer.client.service;

import me.datafox.noterganizer.client.model.WindowSettings;

import java.net.HttpCookie;

/**
 * The settings service stores client-side persistent settings with the
 * {@link java.util.prefs.Preferences} API.
 *
 * @author datafox
 */
public interface SettingsService {
    /**
     * @return address of the last connected server, or null if auto-connect is off
     */
    String getAddress();

    /**
     * @param address address to be persisted
     */
    void setAddress(String address);

    /**
     * @return auto-connect state
     */
    boolean getAutoConnect();

    /**
     * @param autoConnect auto-connect state to be persisted
     */
    void setAutoConnect(boolean autoConnect);

    /**
     * @return remember me HTTP cookie, or null if auto-login is off
     */
    HttpCookie getCookie();

    /**
     * @param cookie remember me HTTP cookie to be persisted
     */
    void setCookie(HttpCookie cookie);

    /**
     * Removes set remember HTTP cookie from persistence
     */
    void removeCookie();

    /**
     * @return window orientation settings
     */
    WindowSettings getWindowSettings();

    /**
     * @param windowSettings window orientation settings to be persisted
     */
    void setWindowSettings(WindowSettings windowSettings);
}
