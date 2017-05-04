package be.kwakeroni.parameters.basic.backend.query.support;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.basic.backend.query.SimpleBackendGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import java.util.*;
import java.util.function.BinaryOperator;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public abstract class SimpleBackendGroupSupport<Q extends BackendQuery<? extends Q, ?>, S, E> implements SimpleBackendGroup<Q> {

    private final String name;
    private final ParameterGroupDefinition definition;
    private final Set<String> parameters;

    public SimpleBackendGroupSupport(String name, ParameterGroupDefinition definition, String... parameters) {
        this(name, definition, new LinkedHashSet<>(Arrays.asList(parameters)));
    }

    public SimpleBackendGroupSupport(String name, ParameterGroupDefinition definition, Set<String> parameters) {
        this.parameters = Collections.unmodifiableSet(Objects.requireNonNull(parameters, "parameters"));
        this.name = Objects.requireNonNull(name, "name");
        this.definition = definition;//Objects.requireNonNull(definition, "definition");

        if (parameters.isEmpty()) {
            throw new IllegalArgumentException("parameters cannot be empty");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ParameterGroupDefinition getDefinition() {
        return definition;
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

    protected E validateNewEntry(E entry, S storage) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleBackendGroupSupport<?, ?, ?> that = (SimpleBackendGroupSupport<?, ?, ?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters);
    }
}
