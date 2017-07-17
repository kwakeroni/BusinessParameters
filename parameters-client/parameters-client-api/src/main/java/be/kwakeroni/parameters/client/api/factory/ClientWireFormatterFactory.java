package be.kwakeroni.parameters.client.api.factory;

import be.kwakeroni.parameters.client.api.query.ClientWireFormatter;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ClientWireFormatterFactory {

    public String getWireFormat();

    public void visitInstances(Visitor visitor);

    @FunctionalInterface
    public static interface Visitor {
        public <I extends ClientWireFormatter> void visit(Class<? super I> type, I instance);
    }
}
