package be.kwakeroni.parameters.basic.definition.es;

import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroupFactory;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchPostFilterRangedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchQueryBasedRangedGroup;
import be.kwakeroni.parameters.basic.definition.factory.RangedDefinitionVisitor;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by kwakeroni on 24/04/17.
 */
public class ElasticSearchRangedGroupFactory implements RangedDefinitionVisitor<ElasticSearchGroup>, ElasticSearchGroupFactory {

    @Override
    public void visit(Registry registry) {
        registry.register(RangedDefinitionVisitor.class, this);
    }

    @Override
    public <T extends Comparable<? super T>> ElasticSearchGroup visit(Definition definition, ParameterType<T> type, ElasticSearchGroup subGroup) {
        return new ElasticSearchPostFilterRangedGroup(
                definition.getRangeParameter(),
                Ranges.stringRangeTypeOf(type),
                definition.getDefinition(),
                subGroup
        );
    }

    @Override
    public <T> ElasticSearchGroup visit(Definition definition, ParameterType<T> type, Comparator<? super T> comparator, ElasticSearchGroup subGroup) {
        return new ElasticSearchPostFilterRangedGroup(
                definition.getRangeParameter(),
                Ranges.stringRangeTypeOf(type, comparator),
                definition.getDefinition(),
                subGroup
        );
    }

    @Override
    public <T, B> ElasticSearchGroup visit(Definition definition, BasicType<T, B> type, ElasticSearchGroup subGroup) {
        Function<String, B> stringConverter = ((Function<String, T>) type::fromString).andThen(type::toBasic);

        return new ElasticSearchQueryBasedRangedGroup(
                definition.getRangeParameter(),
                TypeMapping.getElasticSearchType(type),
                stringConverter,
                definition.getDefinition(),
                subGroup
        );
    }

    @Override
    public <T, B> ElasticSearchGroup visit(Definition definition, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType, ElasticSearchGroup subGroup) {
        Function<String, B> stringConverter = ((Function<String, T>) type::fromString).andThen(converter);

        return new ElasticSearchQueryBasedRangedGroup(
                definition.getRangeParameter(),
                TypeMapping.getElasticSearchType(basicType),
                stringConverter,
                definition.getDefinition(),
                subGroup
        );
    }
}
