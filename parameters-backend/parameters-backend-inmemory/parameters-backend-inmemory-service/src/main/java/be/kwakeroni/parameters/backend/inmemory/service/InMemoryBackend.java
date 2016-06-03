package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.inmemory.api.BusinessParametersBackend;
import be.kwakeroni.parameters.backend.inmemory.api.DataQuery;
import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.client.connector.InternalizationContext;
import be.kwakeroni.parameters.client.connector.QueryInternalizer;

import java.util.*;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryBackend implements BusinessParametersBackend {

    private final Map<String, GroupData> data;
    private final DefaultInteralizationContext internalizationContext;


    /**
     * @deprecated This public zero-argument constructor is exposed for framework construction
     * @see ServiceLoader
     */
    @Deprecated
    public InMemoryBackend(){
        this.data = new HashMap<>();
        ServiceLoader<QueryInternalizer> loader = ServiceLoader.load(QueryInternalizer.class);
        this.internalizationContext = new DefaultInteralizationContext(loader);
    }

    public InMemoryBackend(Map<String, GroupData> data, QueryInternalizer... internalizers) {
        this.data = data;
        this.internalizationContext = new DefaultInteralizationContext(internalizers);
    }

    @Override
    public Object get(String group, Object queryObject) {
        GroupData groupData = getGroupData(group);
        DataQuery<?> query = internalizationContext.internalize(groupData.getEntrySet(), queryObject);
        return query.apply(groupData.getEntries()).orElse(null);
    }

    private GroupData getGroupData(String name){
        return Optional.ofNullable(data.get(name))
                       .orElseThrow(() -> new IllegalArgumentException("No group defined with name " + name));
    }
}
