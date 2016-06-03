package be.kwakeroni.parameters.client.connector;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface InternalizationContext<Q> {

    Q internalize(EntrySet<Q> entrySet, Object query);

}
