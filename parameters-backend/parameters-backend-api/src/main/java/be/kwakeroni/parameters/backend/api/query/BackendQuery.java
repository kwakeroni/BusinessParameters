package be.kwakeroni.parameters.backend.api.query;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface BackendQuery<Q, V> {

    Q raw();

    Object externalizeValue(V value, BackendWireFormatterContext wireFormatterContext);

    V internalizeValue(Object value, BackendWireFormatterContext wireFormatterContext);
}
