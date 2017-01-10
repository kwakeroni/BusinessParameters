package be.kwakeroni.parameters.api.client.query;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ClientWireFormatterContext {

    public <F extends ClientWireFormatter> F getWireFormatter(Class<F> type);

}
