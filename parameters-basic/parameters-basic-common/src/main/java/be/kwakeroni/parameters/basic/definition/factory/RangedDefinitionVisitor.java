package be.kwakeroni.parameters.basic.definition.factory;

import be.kwakeroni.parameters.definition.api.DefinitionVisitor;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.BasicType;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Created by kwakeroni on 14.04.17.
 */
public interface RangedDefinitionVisitor<G> extends DefinitionVisitor<G> {

    public <T extends Comparable<? super T>> G visit(RangedDefinitionVisitor.Definition definition, ParameterType<T> type, G subGroup);

    public <T> G visit(RangedDefinitionVisitor.Definition definition, ParameterType<T> type, Comparator<? super T> comparator, G subGroup);

    public <T, B> G visit(RangedDefinitionVisitor.Definition definition, BasicType<T, B> type, G subGroup);

    public <T, B> G visit(RangedDefinitionVisitor.Definition definition, ParameterType<T> type, Function<T, B> converter, BasicType<B, B> basicType, G subGroup);

    public static <G> RangedDefinitionVisitor<G> from(DefinitionVisitorContext<G> context) {
        return (RangedDefinitionVisitor<G>) context.getVisitor(RangedDefinitionVisitor.class);
    }

    public interface Definition {
        public ParameterGroupDefinition getDefinition();
        public String getRangeParameter();
    }
}
