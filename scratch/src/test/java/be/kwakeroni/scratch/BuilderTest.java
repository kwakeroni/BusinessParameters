package be.kwakeroni.scratch;


import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.backend.es.api.ElasticSearchGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.basic.definition.es.ElasticSearchMappedGroupFactory;
import be.kwakeroni.parameters.basic.definition.es.ElasticSearchRangedGroupFactory;
import be.kwakeroni.parameters.basic.definition.es.ElasticSearchSimpleGroupFactory;
import be.kwakeroni.parameters.basic.definition.factory.MappedGroupFactory;
import be.kwakeroni.parameters.basic.definition.factory.RangedGroupFactory;
import be.kwakeroni.parameters.basic.definition.factory.SimpleGroupFactory;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemoryMappedGroupFactory;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemoryRangedGroupFactory;
import be.kwakeroni.parameters.basic.definition.inmemory.InMemorySimpleGroupFactory;
import be.kwakeroni.parameters.definition.api.factory.GroupFactory;
import be.kwakeroni.parameters.definition.api.factory.GroupFactoryContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.tv.MappedRangedTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
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
    public GroupFactoryContext<G> context;


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
                                .map(param -> param.toArray("ElasticSearch : Query", ELASTIC_SEARCH_CONTEXT)),
                        esPostFilter.stream()
                                .map(param -> param.toArray("ElasticSearch : Filter", ELASTIC_SEARCH_CONTEXT))
                )).toArray(Object[][]::new);
    }

    @Test
    public void testBuildsGroupAsExpected() {
        G built = definition.get().createGroup(context);

        System.out.println(constant);
        System.out.println(built);

        assertThat(built.toString()).isEqualTo(constant.toString());
    }

    private static GroupFactoryContext<InMemoryGroup> IN_MEMORY_CONTEXT = contextOf(
            SimpleGroupFactory.class, new InMemorySimpleGroupFactory(),
            MappedGroupFactory.class, new InMemoryMappedGroupFactory(),
            RangedGroupFactory.class, new InMemoryRangedGroupFactory()
    );

    private static GroupFactoryContext<ElasticSearchGroup> ELASTIC_SEARCH_CONTEXT = contextOf(
            SimpleGroupFactory.class, new ElasticSearchSimpleGroupFactory(),
            MappedGroupFactory.class, new ElasticSearchMappedGroupFactory(),
            RangedGroupFactory.class, new ElasticSearchRangedGroupFactory()
    );

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

        public Object[] toArray(String name, GroupFactoryContext<?> context) {
            return new Object[]{name + " : " + definitionClass.getSimpleName(), group, definition, context};
        }
    }

    private static <G, G1 extends GroupFactory<G>, G2 extends GroupFactory<G>, G3 extends GroupFactory<G>> GroupFactoryContext<G> contextOf(Class<G1> k1, G1 v1, Class<G2> k2, G2 v2, Class<G3> k3, G3 v3) {
        Map<Class<?>, ? extends GroupFactory<G>> map = BuilderTest.<Class<?>, GroupFactory<G>>mapOf(k1, v1, k2, v2, k3, v3);
        return new GroupFactoryContext<G>() {
            @Override
            public <GBF extends GroupFactory<G>> GBF getFactory(Class<GBF> type) {
                return type.cast(map.get(type));
            }
        };
    }

    private static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return Collections.unmodifiableMap(map);
    }
}
