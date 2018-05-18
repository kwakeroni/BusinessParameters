package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.api.BackendEntry;
import be.kwakeroni.parameters.backend.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.api.query.BackendQuery;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.EntryModification;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryQuery;
import be.kwakeroni.parameters.backend.inmemory.factory.InMemoryBackendGroupFactoryContext;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackend implements BusinessParametersBackend<InMemoryQuery<?>> {

    Logger LOG = LoggerFactory.getLogger(InMemoryBackend.class);

    private final Map<String, GroupData> data;
    private final InMemoryBackendGroupFactoryContext factoryContext;
    private final Supplier<Stream<ParameterGroupDefinition<?>>> definitions;
    private final GroupDataStore dataStore;

    public InMemoryBackend(InMemoryBackendGroupFactoryContext factoryContext, Supplier<Stream<ParameterGroupDefinition<?>>> definitions, GroupDataStore dataStore) {
        this.data = new HashMap<>();
        this.factoryContext = factoryContext;
        this.definitions = definitions;
        this.dataStore = dataStore;
    }

    private GroupData getGroupData(String name) {
        GroupData groupData = data.computeIfAbsent(name, this::retrieveGroupData);

        return Optional.ofNullable(groupData)
                .orElseThrow(() -> new IllegalArgumentException("No group defined with name " + name));
    }

    private GroupData retrieveGroupData(String groupName) {
        InMemoryGroup group = defineGroup(groupName);
        return this.dataStore.getGroupData(group);
    }

    private InMemoryGroup defineGroup(String name) {
        return this.definitions.get()
                .filter(definition -> name.equals(definition.getName()))
                .findAny()
                .map(this::defineGroup)
                .orElseThrow(() -> new IllegalStateException("No definition found for group " + name));
    }

    private InMemoryGroup defineGroup(ParameterGroupDefinition<?> definition) {
        return definition.apply(this.factoryContext);
    }

    @Override
    public BackendQuery<? extends InMemoryQuery<?>, ?> internalizeQuery(String groupName, Object queryObject, BackendWireFormatterContext context) {
        return getGroupData(groupName).getGroup().internalize(queryObject, context);
    }

    @Override
    public <V> V select(String group, BackendQuery<? extends InMemoryQuery<?>, V> query) {
        return getValue(query, getGroupData(group));
    }

    private <T> T getValue(BackendQuery<? extends InMemoryQuery<?>, T> query, GroupData groupData) {
        return (T) query.raw().apply(groupData.getEntries()).orElse(null);
    }

    @Override
    public <V> void update(String group, BackendQuery<? extends InMemoryQuery<?>, V> query, V value) {
        GroupData groupData = getGroupData(group);
        setValue(value, query, groupData);
    }

    private <T> void setValue(T value, BackendQuery<? extends InMemoryQuery<?>, T> query, GroupData groupData) {
        EntryModification modification = ((InMemoryQuery<T>) query.raw()).getEntryModification(value, groupData.getEntries());
        groupData.modifyEntry(modification.getEntry(), modification.getModifier());
    }

    @Override
    public EntryData getEntry(String group, String id) {
        GroupData groupData = getGroupData(group);
        return groupData.getEntries()
                .filter(e -> id.equals(e.getId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Entry with id " + id + " not found"));
    }

    @Override
    public void update(String group, String id, Map<String, String> entry) {
        GroupData groupData = getGroupData(group);
        EntryData entryById = getEntry(group, id);
        EntryModification modification = new EntryModification() {
            @Override
            public EntryData getEntry() {
                return entryById;
            }

            @Override
            public Consumer<EntryData> getModifier() {
                return data -> entry.forEach(data::setValue);
            }
        };
        groupData.modifyEntry(modification.getEntry(), modification.getModifier());
    }

    public void insert(String group, Map<String, String> entry) {
        GroupData groupData = getGroupData(group);
        EntryData entryData = DefaultEntryData.of(entry);
        groupData.addEntry(entryData);
    }

    public <R> R exportEntries(String groupName, Collector<? super BackendEntry, ?, R> collector) {
        return getGroupData(groupName)
                .getEntries()
                .collect(collector);
    }

    @Override
    public Collection<String> getGroupNames() {
        return definitions.get().map(ParameterGroupDefinition::getName).collect(Collectors.toSet());
    }

    @Override
    public ParameterGroupDefinition<?> getDefinition(String groupName) {
        return getGroupData(groupName).getGroup().getDefinition();
    }
}
