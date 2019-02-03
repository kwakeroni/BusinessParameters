package be.kwakeroni.parameters.core.support.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DefaultRegistry<T> implements Registry<T> {


    /*
     * <I extends T, J super I> Map<Class<J>, T>
     * The values of type I registered for key Class<J> are instances of J as well as T
     */
    private final Map<Class<?>, T> instances = new HashMap<>(2);

    public <I extends T> void registerInstance(Class<? super I> type, I instance) {
        this.instances.merge(type, instance,
                (one, two) -> {
                    throw new IllegalStateException(String.format("Registered two instances for type %s (%s and %s)", type, one, two));
                });
    }

    public <I extends T> void unregisterInstance(Class<? super I> type, I instance) {
        this.instances.remove(type, instance);
    }

    public <J> Optional<J> get(Class<J> type) {
        Object instance = this.instances.get(type);
        if (instance != null) {
            return Optional.of(type.cast(instance));
        } else {
            return Optional.empty();
        }
    }

    public boolean isEmpty() {
        return instances.isEmpty();
    }

    public Stream<Class<?>> keys() {
        return instances.keySet().stream();
    }

    public Stream<T> instances() {
        return instances.values().stream();
    }

}
