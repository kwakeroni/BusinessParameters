package be.kwakeroni.parameters.connector.direct;

import be.kwakeroni.parameters.api.client.query.ExternalizationContext;
import be.kwakeroni.parameters.api.client.query.Externalizer;

import java.util.HashMap;
import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class ExternalizerRegistry implements ExternalizationContext {

    private Map<Class<?>, Object> objects = new HashMap<>(2);

    public <E extends Externalizer> void register(Class<? super E> type, E externalizer){
        this.objects.put(type, externalizer);
    }

    @Override
    public <E extends Externalizer> E getExternalizer(Class<E> type) {
        Object externalizer = this.objects.get(type);
        if (externalizer != null) {
            return type.cast(externalizer);
        } else {
            throw new IllegalStateException("No externalizer of type " + type.getName() + " registered");
        }
    }
}
