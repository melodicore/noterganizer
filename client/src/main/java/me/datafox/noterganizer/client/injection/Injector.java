package me.datafox.noterganizer.client.injection;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.Singular;
import me.datafox.noterganizer.client.exception.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A relatively simple dependency injector.
 *
 * @see InheritanceResolver
 * @see Component
 * @see Inject
 * @author datafox
 */
public class Injector {
    private final Logger logger;

    private final Map<Class<?>,PerComponentFactory<?>> factoryMap;

    private final Map<Class<?>, Object> beanMap;

    /**
     * @param beans externally instantiated beans
     * @param factories functions to initialize component-specific beans
     */
    @lombok.Builder
    private Injector(@Singular Collection<Object> beans,
                     @Singular Collection<PerComponentFactory<?>> factories) {
        logger = LogManager.getLogger(getClass());

        logger.info("Starting injector initialization");
        Map<Class<?>, PerComponentFactory<?>> tempFactoryMap = factories
                .stream()
                .collect(Collectors.toMap(
                        PerComponentFactory::getType,
                        o -> o));

        logger.info("Initialize supporting classes");
        ClassGraph classGraph = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo();
        InheritanceResolver inheritanceResolver = new InheritanceResolver();

        logger.info("Find all components from classpath");
        List<Class<?>> components;
        try(ScanResult result = classGraph.scan()) {
            components = result
                    .getClassesWithAnnotation(Component.class)
                    .loadClasses();
        }

        //Make externalBeans a mutable set and add self to it
        beans = new HashSet<>(beans);
        beans.add(this);

        //Create a set and populate it with all bean class definitions
        Set<Class<?>> classes = new HashSet<>(components);
        classes.addAll(beans.stream().map(Object::getClass).toList());
        classes.forEach(aClass -> {
            if(tempFactoryMap.containsKey(aClass)) {
                throw new BeanConflictException("The component or bean " + aClass.getName() + " conflicts with a factory");
            }
        });
        classes.addAll(tempFactoryMap.keySet());

        /*
         * Resolve inheritance. The end result is a map where every superclass and interface of a provided class
         * is a key in a map, pointing to the provided class itself. Whenever a collision would happen, like with
         * the Object class, the collision and all of its superclasses and interfaces are removed from the result.
         * This way, any class definition can only refer to exactly 0 or 1 beans.
         */
        logger.info("Resolve inheritance");
        Map<Class<?>, Class<?>> inheritanceMap = inheritanceResolver.resolveInheritance(classes.stream());

        //Initialise the final bean and factory maps and add already instantiated external beans to the bean map.
        beanMap = new HashMap<>(mapBeansToClasses(beans, inheritanceMap));
        factoryMap = new HashMap<>(mapFactoriesToClasses(tempFactoryMap, inheritanceMap));

        logger.info("Check constructor validity and cyclic dependencies");
        components.stream()
                .peek(component -> checkConstructorValidity(component, inheritanceMap))
                .forEach(component -> checkCyclicDependencies(component, inheritanceMap));


        logger.info("Prioritizing component classes to be instantiated");
        components = sortClasses(components, inheritanceMap, beanMap.keySet(), factoryMap);

        /*
         * Instantiate components and add them (and all their valid superclasses and interfaces) to the bean map.
         * A forEach loop with put is used instead of putAll because any required dependencies are retrieved from
         * the map during instantiation. Yay for lazy evaluation in streams.
         */
        logger.info("Instantiating components");
        components.stream()
                .map(this::newInstance)
                .flatMap(component -> mapObjectToClasses(component, inheritanceMap))
                .forEach(entry -> beanMap.put(entry.getKey(), entry.getValue()));
    }

    /**
     * Retrieve an instantiated bean by its class definition.
     *
     * @param aClass class definition of the component
     * @return component matching the definition, or null if none is present
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> aClass) {
        return (T) beanMap.get(aClass);
    }

    /**
     * Returns a new instance of the provided class definition. The class must have either
     * exactly one constructor annotated with {@link Inject} or a default constructor. All
     * constructor parameters are treated as dependencies and retrieved from the bean map.
     * If no appropriate bean is present, an exception is thrown.
     *
     * @param aClass class to be instantiated
     * @return a new instance of a class
     * @throws ConstructorValidityException if multiple constructors annotated with
     *                                      {@link Inject} are present
     * @throws NullBeanException if no appropriate dependency bean is present
     * @throws InjectorInstantiationException if the class could not be instantiated
     */
    public <T> T newInstance(Class<T> aClass) {
        //Get constructor annotated with @Inject
        Optional<Constructor<T>> constructorOptional = getInjectConstructor(aClass);

        //If no constructor with @Inject is present, attempt to instantiate default constructor and return it
        if(constructorOptional.isEmpty()) return instantiateDefaultConstructor(aClass);

        Constructor<T> constructor = constructorOptional.get();

        //Retrieve all dependencies from the bean map and throw an exception if not all are present
        Object[] args = Arrays
                .stream(constructor.getParameterTypes())
                .peek(dependency -> {
                    if(!beanMap.containsKey(dependency) && !factoryMap.containsKey(dependency)) {
                        throw new NullBeanException("No bean found for class " + dependency.getName());
                    }
                })
                .map(dependency -> getDependency(dependency, aClass))
                .toArray(Object[]::new);

        try {
            //Instantiate a new class and return it
            return constructor.newInstance(args);
        } catch(InvocationTargetException |
                InstantiationException |
                IllegalAccessException e) {
            //Lazy error handling
            throw new InjectorInstantiationException(e);
        }
    }

    private Object getDependency(Class<?> dependency, Class<?> component) {
        if(beanMap.containsKey(dependency)) {
            return beanMap.get(dependency);
        }

        return factoryMap.get(dependency).apply(component);
    }

    /**
     * @param beans beans
     * @param inheritanceMap inheritance map from {@link InheritanceResolver}
     * @return map with all unique bean superclasses and interfaces as keys associated with the bean
     */
    private Map<Class<?>, Object> mapBeansToClasses(Collection<?> beans, Map<Class<?>,Class<?>> inheritanceMap) {
        return beans.stream()
                .flatMap(object -> mapObjectToClasses(object, inheritanceMap))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    private Map<Class<?>,PerComponentFactory<?>> mapFactoriesToClasses(Map<Class<?>,PerComponentFactory<?>> factoryMap, Map<Class<?>,Class<?>> inheritanceMap) {
        return factoryMap.keySet().stream()
                .flatMap(aClass -> getInheritanceClasses(aClass, inheritanceMap))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> factoryMap.get(entry.getValue())));
    }

    /**
     * @param object object
     * @param inheritanceMap inheritance map from {@link InheritanceResolver}
     * @return map entries with all unique superclasses and interfaces as keys associated with the object
     */
    private Stream<Map.Entry<Class<?>, Object>> mapObjectToClasses(Object object, Map<Class<?>,Class<?>> inheritanceMap) {
        return getInheritanceClasses(object.getClass(), inheritanceMap)
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), object));
    }

    /**
     * @param aClass class
     * @param inheritanceMap inheritance map from {@link InheritanceResolver}
     * @return map entries with all unique superclasses and interfaces as keys associated with the class
     */
    private Stream<Map.Entry<Class<?>,Class<?>>> getInheritanceClasses(Class<?> aClass, Map<Class<?>, Class<?>> inheritanceMap) {
        return inheritanceMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(aClass));
    }

    /**
     * Make sure that all components have a valid constructor. A class must have exactly 0 or 1 constructors
     * annotated with {@link Inject}. If no constructor is annotated with {@link Inject}, a default constructor
     * must be present. If one constructor is annotated with {@link Inject}, all of its parameters must be beans
     * handled by the injector.
     *
     * @param componentClass class to check
     * @param inheritanceMap inheritance map from {@link InheritanceResolver}
     * @throws ConstructorValidityException if no valid constructor is present
     */
    private void checkConstructorValidity(Class<?> componentClass, Map<Class<?>,Class<?>> inheritanceMap) {
        logger.debug("Checking constructor validity for class " + componentClass.getName());

        componentClass = inheritanceMap.get(componentClass);

        logger.debug("Checking injectable constructor availability");
        Optional<? extends Constructor<?>> constructor = getInjectConstructor(componentClass);

        //If no constructor with @Inject is present, check that a default constructor is present
        if(constructor.isEmpty()) {
            logger.debug("No injectable constructor found, checking default constructor availability");
            try {
                componentClass.getConstructor();
            } catch(NoSuchMethodException e) {
                throw new ConstructorValidityException(componentClass.getName() + " has no valid constructor.\n" +
                        "A default constructor or a constructor annotated with @Inject is required");
            }
            return;
        }

        //Collect all classes from the constructor that are not present in the inheritance map
        logger.debug("Injectable constructor found, checking parameter types");
        List<Class<?>> invalid = Arrays.stream(constructor.get().getParameterTypes())
                .filter(Predicate.not(inheritanceMap.keySet()::contains))
                .toList();

        //If any exist, throw an exception
        if(!invalid.isEmpty()) {
            throw new ConstructorValidityException("The constructor marked with @Inject in " +
                    componentClass.getName() + " has following dependencies which could not be resolved:\n" +
                    invalid.stream().map(Class::getName).collect(Collectors.joining(",\n")));
        }
    }

    /**
     * @param componentClass class
     * @param inheritanceMap inheritance map from {@link InheritanceResolver}
     * @throws CyclicDependencyException if a cyclic dependency is present
     */
    private void checkCyclicDependencies(Class<?> componentClass, Map<Class<?>,Class<?>> inheritanceMap) {
        logger.debug("Checking cyclic dependencies for class " + componentClass);

        componentClass = inheritanceMap.get(componentClass);

        if(getInjectConstructor(componentClass).isEmpty()) return;

        Stack<Class<?>> visited = new Stack<>();

        visited.push(componentClass);

        checkCyclicDependency(visited, inheritanceMap);
    }

    /**
     * @param visited classes that have been visited
     * @param inheritanceMap inheritance map from {@link InheritanceResolver}
     * @throws CyclicDependencyException if a cyclic dependency is present
     */
    private void checkCyclicDependency(Stack<Class<?>> visited, Map<Class<?>,Class<?>> inheritanceMap) {
        Optional<? extends Constructor<?>> constructor = getInjectConstructor(visited.peek());
        
        if(constructor.isEmpty()) return;

        //Iterate through dependencies
        for(Class<?> dependency : constructor.get().getParameterTypes()) {
            //Get the actual dependency class in case a superclass or interface is used
            dependency = inheritanceMap.get(dependency);

            //If the dependency has already been visited, throw an exception
            if(visited.contains(dependency)) {
                throw new CyclicDependencyException(parseCyclicDependency(dependency, visited));
            }

            //Push the dependency to the visited stack, do some recursion magic and pop it back out
            visited.push(dependency);
            checkCyclicDependency(visited, inheritanceMap);
            visited.pop();
        }
    }

    /**
     * Creates a textual representation of a detected cyclic dependency to be attached to an exception.
     *
     * @param dependency cyclic dependency
     * @param stack stack of visited classes
     * @return textual representation of the dependency cycle
     */
    private String parseCyclicDependency(Class<?> dependency, Stack<Class<?>> stack) {
        List<Class<?>> classes = new ArrayList<>(stack.subList(stack.indexOf(dependency), stack.size()));
        classes.add(dependency);
        return "Cyclic dependency detected:\n" +
                classes.stream().map(Class::getName).collect(Collectors.joining(" ->\n"));
    }

    /**
     * Sorts components based on their dependencies.
     *
     * @param components     component classes to be sorted
     * @param inheritanceMap inheritance map from {@link InheritanceResolver}
     * @param externalBeans  classes for external pre-instantiated beans
     * @param factories      classes for external factories
     * @return list of components sorted to the optimal instantiation order
     */
    private List<Class<?>> sortClasses(List<Class<?>> components, Map<Class<?>,Class<?>> inheritanceMap, Collection<Class<?>> externalBeans, Map<Class<?>,PerComponentFactory<?>> factories) {
        //Class keys are associated with integers representing their priority
        Map<Class<?>,Integer> priorityMap = new HashMap<>();

        //Add external beans to the map with the priority of 0 since they are already instantiated
        priorityMap.putAll(externalBeans.stream().collect(Collectors.toMap(c -> c, c -> 0)));
        priorityMap.putAll(factories.keySet().stream().collect(Collectors.toMap(c -> c, c -> 0)));

        //Components that have not been checked yet
        Set<Class<?>> unchecked = new HashSet<>(components);

        //Loop until all components have been checked
        do {
            //Loop through all unchecked components
            for(Class<?> aClass : unchecked) {
                logger.debug("Checking if class " + aClass.getName() + " can be instantiated");
                //Get a constructor annotated with @Inject but only if it has non-zero parameters
                Optional<? extends Constructor<?>> optionalConstructor = getInjectConstructor(aClass)
                        .filter(constructor -> constructor.getParameterCount() != 0);

                //If no constructor is present, add with priority of 0 and continue loop
                if(optionalConstructor.isEmpty()) {
                    logger.debug("Class can be instantiated without dependencies, setting priority to 0");
                    priorityMap.putAll(getInheritanceClassesWithPriority(aClass, 0, inheritanceMap));
                    continue;
                }

                /*
                 * If all dependencies are present in the priority map, add the class to the priority map with
                 * its priority being the maximum priority of the dependencies plus 1.
                 */
                List<Class<?>> args = List.of(optionalConstructor.get().getParameterTypes());
                if(args.stream().allMatch(priorityMap::containsKey)) {
                    int priority = args.stream().mapToInt(priorityMap::get).max().orElse(0) + 1;

                    logger.debug("Class can be instantiated with dependencies, setting priority to " + priority);
                    priorityMap.putAll(getInheritanceClassesWithPriority(aClass, priority, inheritanceMap));
                }
            }

            //Remove all checked classes from the set of unchecked classes
            unchecked.removeAll(priorityMap.keySet());
        } while(!unchecked.isEmpty());
        logger.info("All components have been prioritized successfully");

        List<Class<?>> sortedList = new ArrayList<>(components);
        sortedList.sort(Comparator.comparingInt(priorityMap::get));
        return sortedList;
    }

    /**
     * @param aClass class
     * @param priority instantiation priority
     * @param inheritanceMap inheritance map from {@link InheritanceResolver}
     * @return map with all unique superclasses and interfaces of the class as keys associated with the priority
     */
    private Map<Class<?>,Integer> getInheritanceClassesWithPriority(Class<?> aClass, int priority, Map<Class<?>,Class<?>> inheritanceMap) {
        return getInheritanceClasses(aClass, inheritanceMap)
                .map(Map.Entry::getKey)
                .collect(Collectors.toMap(
                        key -> key,
                        ignored -> priority));
    }

    /**
     * @param aClass class
     * @return Optional containing the constructor annotated with {@link Inject} or empty if none is present
     * @throws ConstructorValidityException if multiple constructors annotated with {@link Inject} are present
     */
    @SuppressWarnings("unchecked")
    private <T> Optional<Constructor<T>> getInjectConstructor(Class<T> aClass) {
        //Get constructors annotated with @Inject
        List<Constructor<T>> annotatedConstructors = Arrays
                .stream(aClass.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                .map(constructor -> (Constructor<T>) constructor)
                .toList();

        //If none are present, return empty
        if(annotatedConstructors.size() == 0) return Optional.empty();

        //If one is present, return it
        if(annotatedConstructors.size() == 1) return Optional.of(annotatedConstructors.get(0));

        //If multiple are present, throw an exception
        throw new ConstructorValidityException("A class may only have one constructor annotated with @Inject, " +
                aClass.getName() + " has multiple");
    }

    /**
     * @param aClass class to be instantiated
     * @return instance instantiated with default constructor
     * @throws ConstructorValidityException if no default constructor is present in the class
     * @throws InjectorInstantiationException if the class could not be instantiated for some other reason
     */
    private <T> T instantiateDefaultConstructor(Class<T> aClass) {
        try {
            return aClass.getConstructor().newInstance();

        } catch(NoSuchMethodException e) {
            throw new ConstructorValidityException(e);
        } catch(InstantiationException |
                IllegalAccessException |
                InvocationTargetException e) {
            throw new InjectorInstantiationException(e);
        }
    }
}
