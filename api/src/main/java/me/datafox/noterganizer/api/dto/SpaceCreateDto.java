package me.datafox.noterganizer.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto object for creating a space.
 * Sent from client to server.
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpaceCreateDto {
    private String name;

    public static SpaceCreateDto of(String name) {
        return builder().name(name).build();
    }
}
