package be.kwakeroni.scratch;


import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.basic.definition.BasicGroupBuilder;
import be.kwakeroni.parameters.basic.definition.es.ElasticSearchBasicGroupBuilder;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemoryBasicGroupBuilder;
import be.kwakeroni.parameters.definition.api.GroupBuilderFactory;
import be.kwakeroni.parameters.definition.api.GroupBuilderFactoryContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.MappedRangedTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
@RunWith(Parameterized.class)
public class BuilderTest<G> {

    @Parameterized.Parameter(0)
    public String name;
    @Parameterized.Parameter(1)
    public G constant;
    @Parameterized.Parameter(2)
    public Supplier<ParameterGroupDefinition> definition;
    @Parameterized.Parameter(3)
    public GroupBuilderFactoryContext<G> context;


    @Parameterized.Parameters(name = "{0}")
    public static Object[][] parameters() {
        List<Param> inMemory = Arrays.asList(
                param(SimpleTVGroup.class, SimpleTVGroup.INMEMORY_GROUP, SimpleTVGroup::new),
                param(MappedTVGroup.class, MappedTVGroup.INMEMORY_GROUP, MappedTVGroup::new),
                param(RangedTVGroup.class, RangedTVGroup.INMEMORY_GROUP, RangedTVGroup::instance),
                param(MappedRangedTVGroup.class, MappedRangedTVGroup.INMEMORY_GROUP, MappedRangedTVGroup::instance)
        );

        List<Param> esQuery = Arrays.asList(
                param(SimpleTVGroup.class, SimpleTVGroup.ELASTICSEARCH_GROUP, SimpleTVGroup::new),
                param(MappedTVGroup.class, MappedTVGroup.ELASTICSEARCH_GROUP, MappedTVGroup::new),
                param(RangedTVGroup.class, RangedTVGroup.elasticSearchGroup(true), RangedTVGroup::withRangeLimits),
                param(MappedRangedTVGroup.class, MappedRangedTVGroup.elasticSearchGroup(true), MappedRangedTVGroup::withRangeLimits)
        );

        List<Param> esPostFilter = Arrays.asList(
                param(RangedTVGroup.class, RangedTVGroup.elasticSearchGroup(false), RangedTVGroup::withoutRangeLimits),
                param(MappedRangedTVGroup.class, MappedRangedTVGroup.elasticSearchGroup(false), MappedRangedTVGroup::withoutRangeLimits)
        );

        return Stream.concat(
                inMemory.stream()
                        .map(param -> param.toArray("InMemory", IN_MEMORY_CONTEXT)),
                Stream.concat(
                        esQuery.stream()
                                .map(param -> param.toArray("ElasticSearch : Query", ELASTIC_SEARCH_QUERY_CONTEXT)),
                        esPostFilter.stream()
                                .map(param -> param.toArray("ElasticSearch : Filter", ELASTIC_SEARCH_POST_FILTER_CONTEXT))
                )).toArray(Object[][]::new);
    }

    @Test
    public void testBuildsGroupAsExpected() {
        G built = definition.get().createGroup(context);

        System.out.println(constant);
        System.out.println(built);

        assertThat(built.toString()).isEqualTo(constant.toString());
    }

    private static GroupBuilderFactoryContext<InMemoryGroup> IN_MEMORY_CONTEXT = new GroupBuilderFactoryContext<InMemoryGroup>() {

        InMemoryBasicGroupBuilder basicGroupBuilder = new InMemoryBasicGroupBuilder();

        @Override
        public <GBF extends GroupBuilderFactory<InMemoryGroup>> GBF getBuilder(Class<GBF> type) {
            if (BasicGroupBuilder.class == (Class<?>) type) {
                return type.cast(basicGroupBuilder);
            }
            return null;
        }
    };

    private static GroupBuilderFactoryContext<ElasticSearchGroup> ELASTIC_SEARCH_POST_FILTER_CONTEXT = new GroupBuilderFactoryContext<ElasticSearchGroup>() {

        ElasticSearchBasicGroupBuilder basicGroupBuilder = new ElasticSearchBasicGroupBuilder();

        @Override
        public <GBF extends GroupBuilderFactory<ElasticSearchGroup>> GBF getBuilder(Class<GBF> type) {
            if (BasicGroupBuilder.class == (Class<?>) type) {
                return type.cast(basicGroupBuilder);
            }
            return null;
        }
    };


    private static GroupBuilderFactoryContext<ElasticSearchGroup> ELASTIC_SEARCH_QUERY_CONTEXT = new GroupBuilderFactoryContext<ElasticSearchGroup>() {

        ElasticSearchBasicGroupBuilder basicGroupBuilder = new ElasticSearchBasicGroupBuilder();

        @Override
        public <GBF extends GroupBuilderFactory<ElasticSearchGroup>> GBF getBuilder(Class<GBF> type) {
            if (BasicGroupBuilder.class == (Class<?>) type) {
                return type.cast(basicGroupBuilder);
            }
            return null;
        }
    };

    private static <D extends ParameterGroupDefinition> Param param(Class<D> definitionClass, BackendGroup<?> group, Supplier<D> definition) {
        return new Param(definitionClass, group, definition);
    }

    private static class Param {
        public final Class<?> definitionClass;
        public final BackendGroup<?> group;
        public final Supplier<? extends ParameterGroupDefinition> definition;

        public Param(Class<?> definitionClass, BackendGroup<?> group, Supplier<? extends ParameterGroupDefinition> definition) {
            this.definitionClass = definitionClass;
            this.group = group;
            this.definition = definition;
        }

        public Object[] toArray(String name, GroupBuilderFactoryContext<?> context) {
            return new Object[]{name + " : " + definitionClass.getSimpleName(), group, definition, context};
        }
    }

}
