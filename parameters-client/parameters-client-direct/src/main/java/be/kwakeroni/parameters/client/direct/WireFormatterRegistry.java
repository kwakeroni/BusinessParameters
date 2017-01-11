package be.kwakeroni.parameters.client.direct;

import be.kwakeroni.parameters.client.api.query.ClientWireFormatterContext;
import be.kwakeroni.parameters.client.api.query.ClientWireFormatter;

import java.util.HashMap;
import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class WireFormatterRegistry implements ClientWireFormatterContext {

    private Map<Class<?>, ClientWireFormatter> objects = new HashMap<>(2);

    public <F extends ClientWireFormatter> void register(Class<? super F> type, F formatter){
        this.objects.put(type, formatter);
    }

    @Override
    public <F extends ClientWireFormatter> F getWireFormatter(Class<F> type) {
        Object formatter = this.objects.get(type);
        if (formatter != null) {
            return type.cast(formatter);
        } else {
            throw new IllegalStateException("No formatter of type " + type.getName() + " registered");
        }
    }
}
