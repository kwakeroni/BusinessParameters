package be.kwakeroni.parameters.basic.backend.query.support;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.basic.backend.query.SimpleBackendGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public abstract class SimpleBackendGroupSupport<Q extends BackendQuery<? extends Q, ?>, S, E> implements SimpleBackendGroup<Q, S, E> {

    private final String name;
    private final Set<String> parameters;

    public SimpleBackendGroupSupport(String name, String... parameters) {
        this(name, new HashSet<>(Arrays.asList(parameters)));
    }

    public SimpleBackendGroupSupport(String name, Set<String> parameters) {
        this.parameters = Collections.unmodifiableSet(Objects.requireNonNull(parameters, "parameters"));
        this.name = Objects.requireNonNull(name, "name");

        if (parameters.isEmpty()) {
            throw new IllegalArgumentException("parameters cannot be empty");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    protected Set<String> getParameterNames() {
        return this.parameters;
    }

    protected abstract boolean hasAnyEntry(S storage);

    protected abstract Map<String, String> asMap(E entry);

    @Override
    public BackendQuery<? extends Q, ?> internalize(Object query, BackendWireFormatterContext context) {
        return context.internalize(this, query);
    }

    @Override
    public E prepareAndValidateNewEntry(E entry, S storage) {
        // Verify entries _can_ be added
//        if (fixedEntries){
//            throw new UnsupportedOperationException("Cannot add entries to group: " + this.getName());
//        }

        if (hasAnyEntry(storage)) {
            throw new IllegalStateException("Cannot add entry to group=" + this.getName());
        }

        // Verify the entry is complete
        Set<String> params = getParameterNames();
        Map<String, String> map = asMap(entry);
        if (!parametersMatch(params, map.keySet())) {
            Collection<String> missing = minus(params, map.keySet());
            Collection<String> unexpected = minus(map.keySet(), params);
            throw new IllegalArgumentException("Incorrect parameters in entry:"
                    + ((missing.isEmpty()) ? "" : " missing: " + missing)
                    + ((unexpected.isEmpty() ? "" : " unexpected: " + unexpected))
            );
        }

        map.forEach((key, value) -> {
            if (value == null) {
                throw new IllegalArgumentException("Parameter values cannot be null: " + key);
            }
        });

        return entry;
    }

    private boolean parametersMatch(Collection<String> expected, Collection<String> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }
        return actual.containsAll(expected) && expected.containsAll(actual);
    }

    private Collection<String> minus(Collection<String> reference, Collection<String> removed) {
        Collection<String> result = new ArrayList<>(reference);
        result.removeAll(removed);
        return result;
    }

    public static <T> BinaryOperator<T> atMostOne() {
        return (e1, e2) -> {
            throw new IllegalStateException(String.format("More than one entry found: %s and %s",
                    e1, e2));
        };
    }

}
