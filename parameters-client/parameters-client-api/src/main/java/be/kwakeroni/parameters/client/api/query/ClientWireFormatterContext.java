package be.kwakeroni.parameters.client.api.query;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ClientWireFormatterContext {

    public <F extends ClientWireFormatter> F getWireFormatter(Class<F> type);

}
