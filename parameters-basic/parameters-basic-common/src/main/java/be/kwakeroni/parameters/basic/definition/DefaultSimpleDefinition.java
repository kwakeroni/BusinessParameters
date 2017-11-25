package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.definition.factory.SimpleDefinitionVisitor;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.ext.PartialDefinition;
import be.kwakeroni.parameters.definition.ext.PartialGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by kwakeroni on 11.04.17.
 */
final class DefaultSimpleDefinition<GroupType extends EntryType> implements SimpleDefinitionVisitor.Definition, PartialDefinition<GroupType, Simple> {

    private final String name;
    private final List<String> parameters;

    DefaultSimpleDefinition(String name, List<String> parameters) {
        this.name = name;
        this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return "basic.simple";
    }

    @Override
    public ParameterGroupDefinition<Simple> getDefinition() {
        return this;
    }

    @Override
    public Collection<String> getParameters() {
        return Collections.unmodifiableCollection(parameters);
    }

    @Override
    public <G> G apply(DefinitionVisitorContext<G> context) {
        return SimpleDefinitionVisitor.from(context).visit(this);
    }

    @Override
    public Simple createGroup(BusinessParameters businessParameters) {
        return createPartial(businessParameters).resolve();
    }

    @Override
    public PartialGroup<GroupType, Simple> createPartial(BusinessParameters businessParameters) {
        return new DefaultSimpleGroup<>(this.name, businessParameters);
    }

}
