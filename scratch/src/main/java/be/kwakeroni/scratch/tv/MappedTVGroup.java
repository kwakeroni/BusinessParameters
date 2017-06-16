package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchMappedGroup;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchSimpleGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryMappedGroup;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

import static be.kwakeroni.parameters.basic.definition.BasicGroup.group;
import static be.kwakeroni.parameters.basic.definition.BasicGroup.mappedGroup;
import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class MappedTVGroup implements ParameterGroup<Mapped<Dag, Simple>> {

    public static final MappedTVGroup instance() {
        return new MappedTVGroup();
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag.type);
    public static Parameter<String> PROGRAM = new DefaultParameter<>("program", STRING);

    public static Entry entry(Dag dag, String program) {
        return Entries.entryOf(DAY, dag, PROGRAM, program);
    }

    // For test purposes
    public static EntryData entryData(Dag day, String program) {
        return DefaultEntryData.of(
                DAY.getName(), day.toString(),
                PROGRAM.getName(), program
        );
    }

    // For test purposes
    public static Query<Mapped<Dag, Simple>, String> programQuery(Dag dag) {
        return valueQuery(dag, MappedTVGroup.PROGRAM);
    }

    public static <T> Query<Mapped<Dag, Simple>, T> valueQuery(Dag dag, Parameter<T> parameter) {
        return new MappedQuery<>(dag, Dag.type,
                new ValueQuery<>(parameter));
    }

    public static Query<Mapped<Dag, Simple>, Entry> entryQuery(Dag dag) {
        return new MappedQuery<>(dag, Dag.type, new EntryQuery());
    }

    private static final String NAME = "tv.mapped";
    public static final ParameterGroupDefinition DEFINITION =
            mappedGroup()
                    .withKeyParameter(DAY.getName())
                    .mappingTo(group()
                            .withParameter(PROGRAM.getName()))
                    .build(NAME);

    static final InmemoryMappedGroup INMEMORY_TEST_GROUP = new InmemoryMappedGroup(DAY.getName(), String::equals, DEFINITION, new InmemorySimpleGroup(NAME, DEFINITION, DAY.getName(), PROGRAM.getName()));

    static final ElasticSearchMappedGroup ELASTICSEARCH_TEST_GROUP =
            new ElasticSearchMappedGroup(DAY.getName(), DEFINITION,
                    new ElasticSearchSimpleGroup(NAME, DEFINITION, DAY.getName(), PROGRAM.getName()));

}
