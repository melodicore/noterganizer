package me.datafox.noterganizer.server.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configuration for a class-specific logger for dependency injection.
 *
 * @author datafox
 */
@Configuration
public class LoggerConfiguration {
    @Bean
    @Scope("prototype")
    public Logger buildLogger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
    }
}
