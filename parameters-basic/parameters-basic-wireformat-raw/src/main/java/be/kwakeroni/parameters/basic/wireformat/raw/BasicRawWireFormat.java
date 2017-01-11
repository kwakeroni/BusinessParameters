package be.kwakeroni.parameters.basic.wireformat.raw;

import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.api.query.BackendWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.basic.backend.query.BasicBackendWireFormatter;
import be.kwakeroni.parameters.basic.client.model.Entry;
import be.kwakeroni.parameters.basic.client.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicRawWireFormat implements BasicClientWireFormatter, BasicBackendWireFormatter {

    Logger LOG = LoggerFactory.getLogger(BasicRawWireFormat.class);

    @Override
    public Object externalizeEntryQuery(EntryQuery query, ClientWireFormatterContext context) {
        return query;
    }

    @Override
    public Object externalizeValueQuery(ValueQuery<?> query, ClientWireFormatterContext context) {
        return query;
    }

    @Override
    public Object externalizeMappedQuery(MappedQuery<?, ?, ?> query, ClientWireFormatterContext context) {
        return query;
    }

    @Override
    public Object externalizeRangedQuery(RangedQuery<?, ?, ?> query, ClientWireFormatterContext context) {
        return query;
    }

    @Override
    public <Q> Optional<Q> tryInternalize(BackendGroup<Q> group, Object query, BackendWireFormatterContext<Q> context) {

        if (query instanceof ValueQuery) {
            ValueQuery<?> valueQuery = (ValueQuery<?>) query;
            return Optional.of(internalizeValueQuery(valueQuery.getParameter().getName(), group, context));
        } else if (query instanceof EntryQuery) {
            return Optional.of(internalizeEntryQuery(group, context));
        } else if (query instanceof RangedQuery) {
            RangedQuery<?, ?, ?> rangedQuery = (RangedQuery<?, ?, ?>) query;
            return Optional.of(internalizeRangedQuery(rangedQuery.getValueString(), rangedQuery.getSubQuery(), group, context));
        } else if (query instanceof MappedQuery) {
            MappedQuery<?, ?, ?> mappedQuery = (MappedQuery<?, ?, ?>) query;
            return Optional.of(internalizeMappedQuery(mappedQuery.getKeyString(), mappedQuery.getSubQuery(), group, context));
        }

        return Optional.empty();
    }


    @Override
    public Entry internalizeEntry(Object result, EntryQuery query, ClientWireFormatterContext context) {
        return (Entry) result;
    }

    @Override
    public Object externalizeEntryResult(Map<String, String> entry) {
        return new DefaultEntry(entry);
    }

    @Override
    public <T> T internalizeValue(Object result, ValueQuery<T> query, ClientWireFormatterContext context) {
        return (result == null)? null : query.getParameter().fromString((String) result);
    }

    @Override
    public Object externalizeValueResult(String value) {
        return (value == null)? null : value;
    }
}
