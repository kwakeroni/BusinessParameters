package be.kwakeroni.parameters.basic.definition;

import be.kwakeroni.parameters.definition.api.GroupBuilderFactory;
import be.kwakeroni.parameters.definition.api.GroupBuilderFactoryContext;

/**
 * Created by kwakeroni on 10.04.17.
 */
public interface BasicGroupBuilder<G> extends GroupBuilderFactory<G> {

    public SimpleGroupBuilder<G> group(String name);

    public MappedGroupBuilder<G> mapped();

    public RangedGroupBuilder<G> ranged();

    public static <G> BasicGroupBuilder<G> from(GroupBuilderFactoryContext<G> context) {
        return context.getBuilder(BasicGroupBuilder.class);
    }
}
