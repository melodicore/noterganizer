package me.datafox.noterganizer.server.controller;

import me.datafox.noterganizer.api.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the version API call.
 *
 * @author datafox
 */
@RestController
public class VersionController {
    @GetMapping("/version")
    public String getVersion() {
        return Constants.IDENTIFIER;
    }
}
