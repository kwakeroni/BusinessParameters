package be.kwakeroni.parameters.basic.definition.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.types.support.BasicType;
import be.kwakeroni.parameters.types.support.JavaLangType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by kwakeroni on 24/04/17.
 */
class TypeMapping {

    static <B> ElasticSearchDataType<B> getElasticSearchType(BasicType<?, B> basicType) {
        ElasticSearchDataType<?> esType = BASIC_TYPE_MAPPING.get(basicType.asBasicType());
        if (esType == null) {
            throw new IllegalArgumentException("Cannot represent Java type " + basicType.asBasicType() + " as ElasticSearch type");
        }
        return (ElasticSearchDataType<B>) esType;
    }

    private static Map<JavaLangType, ElasticSearchDataType<?>> BASIC_TYPE_MAPPING;

    static {
        Map<JavaLangType, ElasticSearchDataType<?>> map = new EnumMap<>(JavaLangType.class);

        map.put(JavaLangType.INT, ElasticSearchDataType.INTEGER);
        // TODO complete

        BASIC_TYPE_MAPPING = Collections.unmodifiableMap(map);
    }

}
