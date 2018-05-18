package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.client.ClientOperation;
import be.kwakeroni.evelyn.model.Event;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.JsonEntryData;
import org.json.JSONObject;

import java.util.function.BiFunction;

public enum GroupTableOperation implements ClientOperation<EntryData> {
    ADD(GroupTableOperation::add),
    REPLACE(GroupTableOperation::replace);

    private BiFunction<EntryData, Event, EntryData> operation;

    GroupTableOperation(BiFunction<EntryData, Event, EntryData> operation) {
        this.operation = operation;
    }

    @Override
    public EntryData operate(EntryData entryData, Event event) {
        return operation.apply(entryData, event);
    }

    private static EntryData add(EntryData entryData, Event event) {
        if (entryData != null) {
            throw new IllegalStateException(String.format("Entry with id '%s' was already added", entryData.getId()));
        }
        if (event.getObjectId() == null) {
            throw new IllegalArgumentException("ADD entry without id");
        }
        return fromString(event.getObjectId(), event.getData());
    }

    private static EntryData replace(EntryData entryData, Event event) {
        if (entryData == null) {
            throw new IllegalStateException(String.format("Cannot REPLACE non-existing entry with id %s", event.getObjectId()));
        }
        if (event.getObjectId() == null || !event.getObjectId().equals(entryData.getId())) {
            throw new IllegalArgumentException(String.format("Cannot REPLACE entry (id %s) by entry with mismatching id %s", entryData.getId(), event.getObjectId()));
        }
        return fromString(entryData.getId(), event.getData());
    }

    static String toString(EntryData entry) {
        if (entry instanceof JsonEntryData) {
            return ((JsonEntryData) entry).toJsonString();
        } else {
            return new JSONObject(entry.asMap()).toString();
        }
    }

    static EntryData fromString(String id, String data) {
        return JsonEntryData.of(id, new JSONObject(data));
    }

}
