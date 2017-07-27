package be.kwakeroni.scratch.tv;


import be.kwakeroni.parameters.backend.api.BackendGroup;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.definition.api.DefinitionVisitorContext;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.env.Services;
import be.kwakeroni.scratch.env.es.ElasticSearchTestData;
import be.kwakeroni.scratch.env.inmemory.InMemoryTestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    public ParameterGroupDefinition definition;
    @Parameterized.Parameter(3)
    public DefinitionVisitorContext<G> context;


    @Parameterized.Parameters(name = "{0}")
    public static Object[][] parameters() {
        List<Param> inMemory = Arrays.asList(
                param(SimpleTVGroup.class, SimpleTVGroup.INMEMORY_TEST_GROUP, SimpleTVGroup.DEFINITION),
                param(MappedTVGroup.class, MappedTVGroup.INMEMORY_TEST_GROUP, MappedTVGroup.DEFINITION),
                param(RangedQueryTVGroup.class, RangedQueryTVGroup.INMEMORY_TEST_GROUP, RangedQueryTVGroup.DEFINITION),
                param(AbstractMappedRangedTVGroup.class, MappedRangedQueryTVGroup.INMEMORY_TEST_GROUP, MappedRangedQueryTVGroup.DEFINITION)
        );

        List<Param> esQuery = Arrays.asList(
                param(SimpleTVGroup.class, SimpleTVGroup.ELASTICSEARCH_TEST_GROUP, SimpleTVGroup.DEFINITION),
                param(MappedTVGroup.class, MappedTVGroup.ELASTICSEARCH_TEST_GROUP, MappedTVGroup.DEFINITION),
                param(RangedQueryTVGroup.class, RangedQueryTVGroup.ELASTICSEARCH_TEST_GROUP, RangedQueryTVGroup.DEFINITION),
                param(AbstractMappedRangedTVGroup.class, MappedRangedQueryTVGroup.ELASTICSEARCH_TEST_GROUP, MappedRangedQueryTVGroup.DEFINITION)
        );

        List<Param> esPostFilter = Arrays.asList(
                param(RangedFilterTVGroup.class, RangedFilterTVGroup.ELASTICSEARCH_TEST_GROUP, RangedFilterTVGroup.DEFINITION),
                param(AbstractMappedRangedTVGroup.class, MappedRangedFilterTVGroup.ELASTICSEARCH_TEST_GROUP, MappedRangedFilterTVGroup.DEFINITION)
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
        G built = definition.apply(context);
        assertEqualGroups(built);
    }

    private void assertEqualGroups(G built) {
        System.out.println(constant);
        System.out.println(built);

        assertThat(built.toString()).isEqualTo(constant.toString());
        assertThat(built.getDefinition()).isSameAs(definition);
    }

    private static <G extends ParameterGroup<?>> Param param(Class<G> definitionClass, BackendGroup<?> group, ParameterGroupDefinition definition) {
        return new Param(definitionClass, group, definition);
    }

    private static class Param {
        public final Class<?> definitionClass;
        public final BackendGroup<?> group;
        public final ParameterGroupDefinition definition;

        public Param(Class<?> definitionClass, BackendGroup<?> group, ParameterGroupDefinition definition) {
            this.definitionClass = definitionClass;
            this.group = group;
            this.definition = definition;
        }

        public Object[] toArray(String name, DefinitionVisitorContext<?> context) {
            return new Object[]{name + " : " + definitionClass.getSimpleName(), group, definition, context};
        }
    }


}
