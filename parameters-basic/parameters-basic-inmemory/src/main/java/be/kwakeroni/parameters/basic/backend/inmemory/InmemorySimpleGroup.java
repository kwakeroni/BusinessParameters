package be.kwakeroni.parameters.basic.backend.inmemory;

import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.basic.backend.query.SimpleBackendGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InmemorySimpleGroup implements SimpleBackendGroup<InMemoryQuery<?>, GroupData, EntryData> {

    private final String name;
    private final boolean fixedEntries;
    private final Set<String> parameters;

    public InmemorySimpleGroup(String name, boolean fixedEntries, String... parameters) {
        this(name, fixedEntries, new HashSet<>(Arrays.asList(parameters)));
    }

    public InmemorySimpleGroup(String name, boolean fixedEntries, Set<String> parameters) {
        this.parameters = Objects.requireNonNull(parameters, "parameters");
        this.name = Objects.requireNonNull(name, "name");
        this.fixedEntries = fixedEntries;

        if (parameters.isEmpty()) {
            throw new IllegalArgumentException("parameters cannot be empty");
        }
    }

    @Override
    public BackendQuery<? extends InMemoryQuery<?>, ?> internalize(Object query, BackendWireFormatterContext context) {
        return context.internalize(this, query);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InMemoryQuery<Map<String, String>> getEntryQuery() {
        return EntryInMemoryQuery.INSTANCE;
    }

    @Override
    public InMemoryQuery<String> getValueQuery(String parameterName) {
        return new ValueInMemoryQuery(parameterName);
    }

    static BinaryOperator<EntryData> atMostOne() {
        return (e1, e2) -> {
            throw new IllegalStateException(String.format("More than one entry found: %s and %s",
                    e1, e2));
        };
    }

    @Override
    public void validateNewEntry(EntryData entry, GroupData storage) {
        // Verify entries _can_ be added
//        if (fixedEntries){
//            throw new UnsupportedOperationException("Cannot add entries to group: " + this.getName());
//        }

        if (storage.getEntries().findAny().isPresent()) {
            throw new IllegalStateException("Cannot add entry to group=" + this.getName());
        }

        // Verify the entry is complete
        Map<String, String> map = entry.asMap();
        if (!parametersMatch(this.parameters, map.keySet())) {
            Collection<String> missing = minus(this.parameters, map.keySet());
            Collection<String> unexpected = minus(map.keySet(), this.parameters);
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
}
