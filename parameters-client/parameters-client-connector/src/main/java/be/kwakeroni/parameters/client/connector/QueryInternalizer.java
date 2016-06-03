package be.kwakeroni.parameters.client.connector;

import java.util.Optional;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface QueryInternalizer {

    <Q> Q tryInternalize(Object query, EntrySet<Q> entrySet, InternalizationContext<Q> context);

}
