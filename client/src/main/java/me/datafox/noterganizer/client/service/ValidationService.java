package me.datafox.noterganizer.client.service;

/**
 * The validation service contains methods for validating user-provided
 * text, currently usernames and passwords.
 *
 * @author datafox
 */
public interface ValidationService {
    /**
     * @param username username
     * @return true if username is valid (not blank)
     */
    boolean validateUsername(String username);

    /**
     * @param password password
     * @return true if password is valid (eight or more characters)
     */
    boolean validatePassword(String password);

    /**
     * @param password password
     * @param repeat repeated password
     * @return true if repeated password is valid (is equal to original password)
     */
    boolean validatePasswordRepeat(String password, String repeat);
}
