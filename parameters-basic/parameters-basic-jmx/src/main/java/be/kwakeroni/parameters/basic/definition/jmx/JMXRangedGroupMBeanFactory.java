package be.kwakeroni.parameters.basic.definition.jmx;

import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupBuilder;
import be.kwakeroni.parameters.adapter.jmx.api.JMXGroupMBeanFactory;
import be.kwakeroni.parameters.basic.definition.factory.RangedDefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by kwakeroni on 10/05/17.
 */
public class JMXRangedGroupMBeanFactory implements RangedDefinitionVisitor<JMXGroupBuilder>, JMXGroupMBeanFactory {

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends DefinitionVisitor> getProvidedInterface() {
        return RangedDefinitionVisitor.class;
    }

    private JMXGroupBuilder visit(Definition definition, JMXGroupBuilder subGroup) {

        subGroup.getQuery(Operations.GET_VALUE)
                .pushType(BasicJMXBackendWireFormatter.ACTION_TYPE_RANGED)
                .prependParameter(definition.getRangeParameter());

        subGroup.getQuery(Operations.GET_ENTRY)
                .pushType(BasicJMXBackendWireFormatter.ACTION_TYPE_RANGED)
                .prependParameter(definition.getRangeParameter());

        return subGroup;
    }

    @Override
    public <T extends Comparable<? super T>> JMXGroupBuilder visit(Definition definition, ParameterType<T> type, JMXGroupBuilder subGroup) {
        return visit(definition, subGroup);
    }

    @Override
    public <T> JMXGroupBuilder visit(Definition definition, ParameterType<T> type, Comparator<? super T> comparator, JMXGroupBuilder subGroup) {
        return visit(definition, subGroup);
    }

    @Override
    public <T, B> JMXGroupBuilder visit(Definition definition, BasicType<T, B> type, JMXGroupBuilder subGroup) {
        return visit(definition, subGroup);
    }

    @Override
    public <T, B> JMXGroupBuilder visit(Definition definition, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType, JMXGroupBuilder subGroup) {
        return visit(definition, subGroup);
    }
}
