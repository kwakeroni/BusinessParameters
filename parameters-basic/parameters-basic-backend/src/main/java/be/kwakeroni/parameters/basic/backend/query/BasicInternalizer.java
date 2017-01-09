package be.kwakeroni.parameters.basic.backend.query;


import be.kwakeroni.parameters.api.backend.BackendGroup;
import be.kwakeroni.parameters.api.backend.query.InternalizationContext;
import be.kwakeroni.parameters.api.backend.query.Internalizer;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BasicInternalizer extends Internalizer {

    default <Q> Q internalizeValueQuery(String parameter, BackendGroup<Q> group, InternalizationContext<Q> context) {
        SimpleBackendGroup<Q> simple = group.as(SimpleBackendGroup.class);
        return simple.getValueQuery(parameter);
    }

    default <Q> Q internalizeEntryQuery(BackendGroup<Q> group, InternalizationContext<Q> context) {
        SimpleBackendGroup<Q> simple = group.as(SimpleBackendGroup.class);
        return simple.getEntryQuery();
    }

    default <Q> Q internalizeRangedQuery(String value, Object rawSubQuery, BackendGroup<Q> group, InternalizationContext<Q> context) {
        RangedBackendGroup<Q> ranged = group.as(RangedBackendGroup.class);
        Q subQuery = context.internalize(ranged.getSubGroup(), rawSubQuery);
        return ranged.getEntryQuery(value, subQuery);
    }

    default <Q> Q internalizeMappedQuery(String key, Object rawSubQuery, BackendGroup<Q> group, InternalizationContext<Q> context) {
        MappedBackendGroup<Q> mapped = group.as(MappedBackendGroup.class);
        Q subQuery = context.internalize(mapped.getSubGroup(), rawSubQuery);
        return mapped.getEntryQuery(key, subQuery);
    }

}
