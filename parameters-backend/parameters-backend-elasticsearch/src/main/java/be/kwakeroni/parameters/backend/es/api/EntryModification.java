package be.kwakeroni.parameters.backend.es.api;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface EntryModification {

    public ElasticSearchEntry getOriginalEntry();

    public ElasticSearchEntry modify(ElasticSearchEntry entry);

    public default EntryModification andThenModifiedBy(Consumer<? super ElasticSearchEntry> modifier) {
        return EntryModification.of(this.getOriginalEntry(), e -> {
            ElasticSearchEntry modified = this.modify(e);
            modifier.accept(modified);
            return modified;
        });
    }

    public static Function<ElasticSearchEntry, EntryModification> modifiedBy(Consumer<? super ElasticSearchEntry> modifier) {
        return entry -> EntryModification.of(entry, e -> {
            modifier.accept(e);
            return e;
        });
    }

    public static Function<ElasticSearchEntry, EntryModification> replacedBy(Function<? super ElasticSearchEntry, ? extends ElasticSearchEntry> modifier) {
        return entry -> EntryModification.of(entry, modifier);
    }

    public static EntryModification of(ElasticSearchEntry entry, Function<? super ElasticSearchEntry, ? extends ElasticSearchEntry> modifier) {
        return new EntryModification() {
            @Override
            public ElasticSearchEntry getOriginalEntry() {
                return entry;
            }

            @Override
            public ElasticSearchEntry modify(ElasticSearchEntry entry) {
                return modifier.apply(entry);
            }
        };
    }
}
