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
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.scratch.tv.definition.MappedTV;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class MappedTVGroup extends MappedTV {

    public static final MappedTVGroup instance() {
        return new MappedTVGroup();
    }

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

    static final InmemoryMappedGroup INMEMORY_TEST_GROUP = new InmemoryMappedGroup(DAY.getName(), String::equals, DEFINITION, new InmemorySimpleGroup(NAME, DEFINITION, DAY.getName(), PROGRAM.getName()));

    static final ElasticSearchMappedGroup ELASTICSEARCH_TEST_GROUP =
            new ElasticSearchMappedGroup(DAY.getName(), DEFINITION,
                    new ElasticSearchSimpleGroup(NAME, DEFINITION, DAY.getName(), PROGRAM.getName()));

}
