package be.kwakeroni.parameters.definition.api;

import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.PartialQuery;

import java.util.Collection;

/**
 * Created by kwakeroni on 07.04.17.
 */
public interface ParameterGroupDefinition<GroupType extends EntryType> {

    public String getName();

    public Collection<String> getParameters();

    public String getType();

    public <G> G apply(DefinitionVisitorContext<G> context);

    public default GroupType createGroup(BusinessParameters businessParameters) {
        return createPartial(businessParameters).createGroup(PartialQuery.identity());
    }

    public Partial<GroupType> createPartial(BusinessParameters businessParameters);


    @FunctionalInterface
    public static interface Partial<Type extends EntryType> {
        Type createGroup(PartialQuery<?, Type> query);
    }
}
