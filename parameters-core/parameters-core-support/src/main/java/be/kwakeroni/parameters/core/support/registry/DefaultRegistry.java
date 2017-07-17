package be.kwakeroni.parameters.core.support.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by kwakeroni on 16/06/17.
 */
public class DefaultRegistry<T> implements Registry<T> {


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

    public <I extends T> Optional<I> get(Class<I> type) {
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
