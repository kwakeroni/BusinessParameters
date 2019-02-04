package be.kwakeroni.parameters.basic.definition.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwakeroni on 24/04/17.
 */
class TypeMapping {

    static <B> ElasticSearchDataType<B> getElasticSearchType(BasicType<?, B> basicType) {
        ElasticSearchDataType<?> esType = BASIC_TYPE_MAPPING.get(basicType.getBasicJavaClass());
        if (esType == null) {
            throw new IllegalArgumentException("Cannot represent Java type " + basicType.getBasicJavaClass() + " as ElasticSearch type");
        }
        return (ElasticSearchDataType<B>) esType;
    }

    private static Map<Class<?>, ElasticSearchDataType<?>> BASIC_TYPE_MAPPING;

    static {
        Map<Class<?>, ElasticSearchDataType<?>> map = new HashMap<>();

        map.put(Integer.class, ElasticSearchDataType.INTEGER);
        // TODO complete

        BASIC_TYPE_MAPPING = Collections.unmodifiableMap(map);
    }

}
