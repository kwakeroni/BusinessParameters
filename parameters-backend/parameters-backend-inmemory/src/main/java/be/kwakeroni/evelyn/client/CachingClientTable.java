package be.kwakeroni.evelyn.client;

import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CachingClientTable<E> implements ClientTable<E> {

    private final ClientTable<E> delegate;
    private final Function<String, ? extends ClientOperation<E>> operationMap;
    private final Function<? super E, String> idGetter;
    private Map<String, E> cache;

    public CachingClientTable(DatabaseAccessor accessor, Function<String, ? extends ClientOperation<E>> operationMap, Function<E, String> idGetter) {
        this(new DefaultClientTable<>(accessor, operationMap), operationMap, idGetter);
    }

    CachingClientTable(ClientTable<E> delegate, Function<String, ? extends ClientOperation<E>> operationMap, Function<? super E, String> idGetter) {
        this.delegate = delegate;
        this.operationMap = operationMap;
        this.idGetter = idGetter;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Stream<E> findAll() {
        return getCache().values().stream();
    }

    @Override
    public E findById(String id) {
        return getCache().get(id);
    }

    @Override
    public synchronized Event append(String user, String operation, String objectId, String data) {
        ensureCacheIsLoaded();
        Event event = this.delegate.append(user, operation, objectId, data);
        applyEvent(event);
        return event;
    }

    private Map<String, E> getCache() {
        ensureCacheIsLoaded();
        return cache;
    }

    private synchronized void ensureCacheIsLoaded() {
        if (cache == null) {
            cache = this.delegate.findAll().collect(Collectors.toMap(this.idGetter, Function.identity(), noDuplicates(), HashMap::new));
        }
    }

    private synchronized void applyEvent(Event event) {
        E entity = operationMap.apply(event.getOperation()).operate(findById(event.getObjectId()), event);
        getCache().put(event.getObjectId(), entity);
    }

    private BinaryOperator<E> noDuplicates() {
        return (e1, e2) -> {
            throw new IllegalStateException("Duplicate key: " + this.idGetter.apply(e1));
        };
    }
}
