/**
 * @author datafox
 */

module noterganizer.api {
    exports me.datafox.noterganizer.api;
    exports me.datafox.noterganizer.api.dto;

    requires static lombok;

    opens me.datafox.noterganizer.api.dto;
}