package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.api.client.model.Parameter;
import be.kwakeroni.parameters.api.client.model.ParameterGroup;
import be.kwakeroni.parameters.backend.inmemory.InmemorySimpleGroup;
import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.api.backend.BackendGroup;

import java.util.stream.Stream;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class SimpleTVGroup implements ParameterGroup<Simple> {

    public static final SimpleTVGroup instance(){
        return new SimpleTVGroup();
    }

    @Override
    public String getName() {
        return "tv.simple";
    }

    public static Parameter<Dag> DAY = new DefaultParameter<>("day", Dag::valueOf, Dag::name);
    public static Parameter<Slot> SLOT = new DefaultParameter<>("slot", Slot::fromString, Slot::toString);

    // For test purposes
    public static final GroupData getData(Dag dag, Slot slot){
        InmemorySimpleGroup group = new InmemorySimpleGroup();
        return new GroupData() {
            @Override
            public Stream<EntryData> getEntries() {
                return Stream.of(DefaultEntryData.of(
                    DAY.getName(), dag.toString(),
                    SLOT.getName(), slot.toString()
                ));
            }

            @Override
            public BackendGroup<DataQuery<?>> getGroup() {
                return group;
            }
        };
    }
}
