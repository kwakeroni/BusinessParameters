package be.kwakeroni.parameters.core.support.registry;

import java.util.Optional;

/**
 * Created by kwakeroni on 16/06/17.
 */
public interface Registry<T> {

    public <I extends T> void registerInstance(Class<? super I> type, I instance);

    public <I extends T> void unregisterInstance(Class<? super I> type, I instance);

    public <I extends T> Optional<I> get(Class<I> type);

    public boolean isEmpty();

}
