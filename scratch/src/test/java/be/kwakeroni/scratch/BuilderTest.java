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
    public G constant;
    @Parameterized.Parameter(1)
    public Supplier<ParameterGroupDefinition> definition;
    @Parameterized.Parameter(2)
    public GroupBuilderFactoryContext<G> context;


    @Parameterized.Parameters
    public static Object[][] parameters() {
        List<Param> inMemory = Arrays.asList(
                param(SimpleTVGroup.INMEMORY_GROUP, SimpleTVGroup::new),
                param(MappedTVGroup.INMEMORY_GROUP, MappedTVGroup::new),
                param(RangedTVGroup.INMEMORY_GROUP, RangedTVGroup::instance),
                param(MappedRangedTVGroup.INMEMORY_GROUP, MappedRangedTVGroup::instance)
        );

        List<Param> esPostFilter = Arrays.asList(
                param(SimpleTVGroup.ELASTICSEARCH_GROUP, SimpleTVGroup::new),
                param(MappedTVGroup.ELASTICSEARCH_GROUP, MappedTVGroup::new),
                param(RangedTVGroup.elasticSearchGroup(false), RangedTVGroup::withoutRangeLimits),
                param(MappedRangedTVGroup.elasticSearchGroup(false), MappedRangedTVGroup::withoutRangeLimits)
        );

        List<Param> esQuery = Arrays.asList(
                param(RangedTVGroup.elasticSearchGroup(true), RangedTVGroup::withRangeLimits),
                param(MappedRangedTVGroup.elasticSearchGroup(true), MappedRangedTVGroup::withRangeLimits)
        );

        return Stream.concat(
                inMemory.stream()
                        .map(param -> param.toArray(IN_MEMORY_CONTEXT)),
                Stream.concat(
                        esPostFilter.stream()
                                .map(param -> param.toArray(ELASTIC_SEARCH_POST_FILTER_CONTEXT)),
                        esQuery.stream()
                                .map(param -> param.toArray(ELASTIC_SEARCH_QUERY_CONTEXT))
                )).toArray(Object[][]::new);
    }

    @Test
    public void testBuildsGroupAsExpected() {
        G built = definition.get().createGroup(context).build();

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

    private static final Param param(BackendGroup<?> group, Supplier<ParameterGroupDefinition> definition) {
        return new Param(group, definition);
    }

    private static class Param {
        public final BackendGroup<?> group;
        public final Supplier<ParameterGroupDefinition> definition;

        public Param(BackendGroup<?> group, Supplier<ParameterGroupDefinition> definition) {
            this.group = group;
            this.definition = definition;
        }

        public Object[] toArray(GroupBuilderFactoryContext<?> context) {
            return new Object[]{group, definition, context};
        }
    }

}
