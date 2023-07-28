package me.datafox.noterganizer.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data for window orientation persistence.
 *
 * @author datafox
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WindowSettings {
    private boolean maximized;

    private double x;

    private double y;

    private double width;

    private double height;
}
