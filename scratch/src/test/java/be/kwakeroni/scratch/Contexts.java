package be.kwakeroni.scratch;

import be.kwakeroni.parameters.definition.api.factory.GroupFactory;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwakeroni on 26/04/17.
 */
public class Contexts {

    private Contexts() {

    }

    public static <G, G1 extends GroupFactory<G>, G2 extends GroupFactory<G>, G3 extends GroupFactory<G>> GroupFactoryContext<G> of(Class<G1> k1, G1 v1, Class<G2> k2, G2 v2, Class<G3> k3, G3 v3) {
        Map<Class<?>, ? extends GroupFactory<G>> map = Contexts.<Class<?>, GroupFactory<G>>mapOf(k1, v1, k2, v2, k3, v3);
        return new GroupFactoryContext<G>() {
            @Override
            public <GBF extends GroupFactory<G>> GBF getFactory(Class<GBF> type) {
                return type.cast(map.get(type));
            }
        };
    }

    private static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return Collections.unmodifiableMap(map);
    }
}
