package be.kwakeroni.parameters.backend.api.factory;

import be.kwakeroni.parameters.backend.api.query.BackendWireFormatter;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BackendWireFormatterFactory {

    public String getWireFormat();

    public void visitInstances(Visitor visitor);

    @FunctionalInterface
    public static interface Visitor {
        public <I extends BackendWireFormatter> void visit(Class<? super I> type, I instance);
    }

}
