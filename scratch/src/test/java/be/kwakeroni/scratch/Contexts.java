package be.kwakeroni.scratch;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by kwakeroni on 26/04/17.
 */
public class Contexts {

    private Contexts() {

    }

    public static <G, G1 extends DefinitionVisitor<G>, G2 extends DefinitionVisitor<G>, G3 extends DefinitionVisitor<G>, G4 extends DefinitionVisitor<G>> DefinitionVisitorContext<G> of(Class<G1> k1, G1 v1, Class<G2> k2, G2 v2, Class<G3> k3, G3 v3, Class<G4> k4, G4 v4) {
        Map<Class<?>, ? extends DefinitionVisitor<G>> map = Contexts.<Class<?>, DefinitionVisitor<G>>mapOf(k1, v1, k2, v2, k3, v3, k4, v4);
        return new DefinitionVisitorContext<G>() {
            @Override
            public <GBF extends DefinitionVisitor<G>> GBF getVisitor(Class<GBF> type) {
                return Objects.requireNonNull(type.cast(map.get(type)), () -> "Not found visitor of type " + type.getName());
            }
        };
    }

    private static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return Collections.unmodifiableMap(map);
    }
}
