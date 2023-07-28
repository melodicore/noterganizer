package me.datafox.noterganizer.client.injection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Stream;

/**
 * A helper class for {@link Injector} that looks up inheritance for specified classes
 * and returns it in a map where every superclass and interface class definition is associated
 * with the specified class definition. Whenever a collision might happen where the same
 * superclass or interface would be associated with multiple classes (like with {@link Object}),
 * those are marked as collisions and removed from the map.
 *
 * @author datafox
 */
public class InheritanceResolver {
    private final Logger logger;

    private final Map<Class<?>,Class<?>> classMap;

    private final Set<Class<?>> collisions;

    public InheritanceResolver() {
        logger = LogManager.getLogger(getClass());
        classMap = new HashMap<>();
        collisions = new HashSet<>();
    }

    /**
     * @param classes stream of classes to process
     * @return map where all unique superclasses and interfaces of a class are associated with the class
     */
    public Map<Class<?>, Class<?>> resolveInheritance(Stream<Class<?>> classes) {
        //Clean up if the method is called a second time
        classMap.clear();
        collisions.clear();

        //Resolve inheritance for each class sequentially
        classes.map(this::resolveInheritance).forEach(classMap::putAll);

        logger.info("Inheritance resolved successfully");

        return classMap;
    }

    /**
     * @param aClass class to process
     * @return map where every superclass or interface of the class is associated with the class
     */
    private Map<Class<?>, Class<?>> resolveInheritance(Class<?> aClass) {
        logger.info("Resolving inheritance recursively for " + aClass.getName());

        return new HashMap<>(resolveInheritanceRecursive(aClass, aClass));
    }

    /**
     * Recursive method that looks up all superclasses and interfaces of a class and
     * puts them to a map with a second class as the associated value, but only if they
     * are not marked as collisions already. If any of the superclasses and interfaces
     * are already present within the classMap variable, those are instead marked as collisions
     * along with all their superclasses and interfaces and removed from the classMap.
     *
     * @param current class of whose superclasses and interfaces are looked up
     * @param original class to be used as the associated value in the map
     * @return map where every superclass or interface of the current class is associated with the original class
     */
    private Map<Class<?>,Class<?>> resolveInheritanceRecursive(Class<?> current, Class<?> original) {
        logger.debug("Current class is " + current.getName());

        Map<Class<?>,Class<?>> map = new HashMap<>();

        //If current class is already marked as collision, return empty map
        if(collisions.contains(current)) {
            logger.debug("Current class already marked as a collision");

            return Map.of();
        }

        /*
         * If current class is not marked as a collision but is present in classMap, recursively mark
         * it and all its superclasses and interfaces as collisions, remove them from the classMap and
         * return an empty map.
         */
        if(classMap.containsKey(current)) {
            logger.debug("Marking current class recursively as a collision");

            resolveCollisionsRecursive(current);

            return Map.of();
        }

        //Associate current class with the original class in the map
        map.put(current, original);

        //Recursively call this method for all superclasses and interfaces of the current class
        getParents(current)
                .map(aClass -> resolveInheritanceRecursive(aClass, original))
                .forEach(map::putAll);

        return map;
    }

    /**
     * Removes a class and all its superclasses and interfaces from the classMap and marks
     * them as collisions.
     *
     * @param aClass class to be marked as a collision
     */
    private void resolveCollisionsRecursive(Class<?> aClass) {
        classMap.remove(aClass);

        collisions.add(aClass);

        getParents(aClass).forEach(this::resolveCollisionsRecursive);
    }

    /**
     * @param aClass class
     * @return stream of all direct superclasses and interfaces of the class
     */
    private Stream<Class<?>> getParents(Class<?> aClass) {
        Set<Class<?>> classes = new HashSet<>();

        if(aClass.getSuperclass() != null) {
            classes.add(aClass.getSuperclass());
        }

        classes.addAll(List.of(aClass.getInterfaces()));

        return classes.stream();
    }
}
