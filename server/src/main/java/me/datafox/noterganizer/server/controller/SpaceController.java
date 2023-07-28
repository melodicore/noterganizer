package me.datafox.noterganizer.server.controller;

import me.datafox.noterganizer.api.dto.SpaceCreateDto;
import me.datafox.noterganizer.api.dto.SpaceDto;
import me.datafox.noterganizer.server.service.MappingService;
import me.datafox.noterganizer.server.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller for all space-related API calls.
 *
 * @author datafox
 */
@RestController
public class SpaceController {
    @Autowired
    private SpaceService spaceService;
    
    @Autowired
    private MappingService mappingService;

    @GetMapping("/space/get")
    public ResponseEntity<SpaceDto> getSpace(@RequestParam String uuid,
                                             Principal principal) {

        SpaceDto space = spaceService.getSpaceDto(uuid, principal);

        return ResponseEntity.ok(space);
    }

    @PostMapping("/space/create")
    public ResponseEntity<String> createSpace(@RequestBody SpaceCreateDto dto,
                                              Principal principal) {

        String uuid = spaceService.createSpace(dto, principal);

        return ResponseEntity.ok(uuid);
    }

    @DeleteMapping("/space/remove")
    public ResponseEntity<String> removeSpace(@RequestParam String uuid,
                                              Principal principal) {
        
        spaceService.removeSpace(uuid, principal);

        return ResponseEntity.ok("success");
    }
}
