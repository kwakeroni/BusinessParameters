package be.kwakeroni.parameters.basic.definition.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchDataType;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchMappedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchPostFilterRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.definition.BasicGroupBuilder;
import be.kwakeroni.parameters.basic.definition.MappedGroupBuilder;
import be.kwakeroni.parameters.basic.definition.RangedGroupBuilder;
import be.kwakeroni.parameters.basic.definition.SimpleGroupBuilder;
import be.kwakeroni.parameters.basic.definition.support.MappedGroupBuilderSupport;
import be.kwakeroni.parameters.basic.definition.support.RangedGroupBuilderSupport;
import be.kwakeroni.parameters.basic.definition.support.SimpleGroupBuilderSupport;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;
import be.kwakeroni.parameters.types.support.JavaLangType;

import java.util.*;
import java.util.function.Function;

/**
 * Created by kwakeroni on 12.04.17.
 */
public class ElasticSearchBasicGroupBuilder implements BasicGroupBuilder<ElasticSearchGroup> {

    @Override
    public SimpleGroupBuilder<ElasticSearchGroup> group(String name) {
        class Simple extends SimpleGroupBuilderSupport<ElasticSearchGroup> {
            @Override
            public ElasticSearchGroup createGroup() {
                return new ElasticSearchSimpleGroup(name, new LinkedHashSet<>(getParameters()));
            }
        }

        return new Simple();
    }

    @Override
    public MappedGroupBuilder<ElasticSearchGroup> mapped() {
        class Mapped extends MappedGroupBuilderSupport<ElasticSearchGroup> {
            @Override
            protected ElasticSearchGroup build(ElasticSearchGroup subGroup) {
                return new ElasticSearchMappedGroup(getKeyParameter(), subGroup);
            }
        }
        return new Mapped();

    }

    @Override
    public RangedGroupBuilder<ElasticSearchGroup> ranged() {
        class Ranged extends RangedGroupBuilderSupport<ElasticSearchGroup> {
            @Override
            protected <T extends Comparable<? super T>> ElasticSearchGroup createGroup(ElasticSearchGroup subGroup, ParameterType<T> type) {
                return new ElasticSearchPostFilterRangedGroup(
                        getRangeParameter(),
                        Ranges.stringRangeTypeOf(type),
                        subGroup
                );
            }

            @Override
            protected <T> ElasticSearchGroup createGroup(ElasticSearchGroup subGroup, ParameterType<T> type, Comparator<? super T> comparator) {
                return new ElasticSearchPostFilterRangedGroup(
                        getRangeParameter(),
                        Ranges.stringRangeTypeOf(type, comparator),
                        subGroup
                );
            }

            @Override
            protected <T, B> ElasticSearchGroup createGroup(ElasticSearchGroup subGroup, BasicType<T, B> type) {
                Function<String, B> stringConverter = ((Function<String, T>) type::fromString).andThen(type::toBasic);

                return new ElasticSearchQueryBasedRangedGroup(
                        getRangeParameter(),
                        getElasticSearchType(type),
                        stringConverter,
                        subGroup
                );
            }

            @Override
            protected <T, B> ElasticSearchGroup createGroup(ElasticSearchGroup subGroup, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType) {
                Function<String, B> stringConverter = ((Function<String, T>) type::fromString).andThen(converter);

                return new ElasticSearchQueryBasedRangedGroup(
                        getRangeParameter(),
                        getElasticSearchType(basicType),
                        stringConverter,
                        subGroup
                );
            }
        }
        return new Ranged();
    }

    private <B> ElasticSearchDataType<B> getElasticSearchType(BasicType<?, B> basicType) {
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
