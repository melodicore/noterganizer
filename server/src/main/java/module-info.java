/**
 * @author datafox
 */

module noterganizer.server {
    requires static lombok;

    requires noterganizer.api;

    requires org.slf4j;

    requires spring.beans;

    requires spring.context;

    requires spring.data.mongodb;

    requires spring.security.config;
    requires spring.security.core;
    requires spring.security.crypto;
    requires spring.security.web;

    requires spring.web;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.data.commons;
}