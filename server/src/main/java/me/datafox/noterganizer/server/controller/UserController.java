package me.datafox.noterganizer.server.controller;

import me.datafox.noterganizer.api.dto.UserChangeDto;
import me.datafox.noterganizer.api.dto.UserDto;
import me.datafox.noterganizer.api.dto.UserRegisterDto;
import me.datafox.noterganizer.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller for all user-related API calls.
 *
 * @author datafox
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<UserDto> getUser(Principal principal) {
        return ResponseEntity.ok(userService.getUserDto(principal));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("login");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDto dto) {
        userService.createUser(dto);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/change")
    public ResponseEntity<String> changeUser(@RequestBody UserChangeDto dto, Principal principal) {
        userService.changeUser(dto, principal);
        return ResponseEntity.ok("success");
    }
}
