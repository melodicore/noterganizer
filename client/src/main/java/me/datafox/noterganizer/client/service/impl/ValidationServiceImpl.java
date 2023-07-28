package me.datafox.noterganizer.client.service.impl;

import me.datafox.noterganizer.client.injection.Component;
import me.datafox.noterganizer.client.service.ValidationService;

/**
 * Validation service implementation.
 *
 * @author datafox
 */
@Component
public class ValidationServiceImpl implements ValidationService {
    @Override
    public boolean validateUsername(String username) {
        return !username.isBlank();
    }

    @Override
    public boolean validatePassword(String password) {
        return password.length() >= 8;
    }

    @Override
    public boolean validatePasswordRepeat(String password, String repeat) {
        return password.equals(repeat);
    }
}
