package be.kwakeroni.parameters.test;

import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.parameters.basic.client.connector.BasicExternalizer;
import be.kwakeroni.parameters.basic.wireformat.raw.BasicRawWireFormat;
import be.kwakeroni.parameters.basic.wireformat.standard.BasicStandardWireformat;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.client.model.EntryType;
import be.kwakeroni.parameters.client.model.ParameterGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class Environment {




    private final BusinessParameters parameters;

    private Map<String, GroupData> map = new HashMap<>();
    {
    }

    private final BasicStandardWireformat basicWireFormat = new BasicStandardWireformat();
//    private final BasicRawWireFormat basicWireFormat = new BasicRawWireFormat();
    private final InMemoryBackend backend = new InMemoryBackend(map, basicWireFormat);

    public Environment(){

        this.parameters = new BusinessParameters() {
            @Override
            public <ET extends EntryType, T> T get(ParameterGroup<ET> group, Query<ET, T> query) {
                Object external = query.externalize(Environment.this::getExternalizer);
                return (T) backend.get(group.getName(), external);
            }
        };

        TVProgramGroupData data = new TVProgramGroupData();
        data.add(Dag.MAANDAG, Slot.atHalfPast(8), "Gisteren Zondag");

        map.put(TVProgram.groupName(), data);

    }

    public BusinessParameters getParameters(){
        return this.parameters;
    }

    public <Externalizer> Externalizer getExternalizer(Class<Externalizer> type){
        return type.cast(basicWireFormat);
    }

}
