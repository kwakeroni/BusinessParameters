package be.kwakeroni.scratch.tv;


import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.scratch.ElasticSearchTestData;
import be.kwakeroni.scratch.InMemoryTestData;
import be.kwakeroni.scratch.Services;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
@RunWith(Parameterized.class)
public class BuilderTest<G extends BackendGroup<?>> {

    @Parameterized.Parameter(0)
    public String name;
    @Parameterized.Parameter(1)
    public G constant;
    @Parameterized.Parameter(2)
    public Supplier<ParameterGroupDefinition> definition;
    @Parameterized.Parameter(3)
    public DefinitionVisitorContext<G> context;


    @Parameterized.Parameters(name = "{0}")
    public static Object[][] parameters() {
        List<Param> inMemory = Arrays.asList(
                param(SimpleTVGroup.class, SimpleTVGroup.INMEMORY_TEST_GROUP, SimpleTVGroup::new),
                param(MappedTVGroup.class, MappedTVGroup.INMEMORY_TEST_GROUP, MappedTVGroup::new),
                param(RangedQueryTVGroup.class, RangedQueryTVGroup.INMEMORY_TEST_GROUP, RangedQueryTVGroup::instance),
                param(AbstractMappedRangedTVGroup.class, MappedRangedQueryTVGroup.INMEMORY_TEST_GROUP, MappedRangedQueryTVGroup::instance)
        );

        List<Param> esQuery = Arrays.asList(
                param(SimpleTVGroup.class, SimpleTVGroup.ELASTICSEARCH_TEST_GROUP, SimpleTVGroup::new),
                param(MappedTVGroup.class, MappedTVGroup.ELASTICSEARCH_TEST_GROUP, MappedTVGroup::new),
                param(RangedQueryTVGroup.class, RangedQueryTVGroup.ELASTICSEARCH_TEST_GROUP, RangedQueryTVGroup::instance),
                param(AbstractMappedRangedTVGroup.class, MappedRangedQueryTVGroup.ELASTICSEARCH_TEST_GROUP, MappedRangedQueryTVGroup::instance)
        );

        List<Param> esPostFilter = Arrays.asList(
                param(RangedFilterTVGroup.class, RangedFilterTVGroup.ELASTICSEARCH_TEST_GROUP, RangedFilterTVGroup::instance),
                param(AbstractMappedRangedTVGroup.class, MappedRangedFilterTVGroup.ELASTICSEARCH_TEST_GROUP, MappedRangedFilterTVGroup::instance)
        );

        return Stream.concat(
                inMemory.stream()
                        .map(param -> param.toArray("InMemory", InMemoryTestData.FACTORY_CONTEXT)),
                Stream.concat(
                        esQuery.stream()
                                .map(param -> param.toArray("ElasticSearch : Query", ElasticSearchTestData.FACTORY_CONTEXT)),
                        esPostFilter.stream()
                                .map(param -> param.toArray("ElasticSearch : Filter", ElasticSearchTestData.FACTORY_CONTEXT))
                )).toArray(Object[][]::new);
    }

    @Test
    public void isGroupDefined() {
        Optional<ParameterGroupDefinition> opt = Services.loadDefinition(constant.getName());
        G built = opt.get().apply(context);
        assertEqualGroups(built);
    }

    @Test
    public void testBuildsGroupAsExpected() {
        G built = definition.get().apply(context);
        assertEqualGroups(built);
    }

    private void assertEqualGroups(G built) {
        System.out.println(constant);
        System.out.println(built);

        assertThat(built.toString()).isEqualTo(constant.toString());
    }

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

        public Object[] toArray(String name, DefinitionVisitorContext<?> context) {
            return new Object[]{name + " : " + definitionClass.getSimpleName(), group, definition, context};
        }
    }


}
