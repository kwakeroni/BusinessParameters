package be.kwakeroni.parameters.backend.inmemory.service;

import be.kwakeroni.parameters.backend.inmemory.api.GroupData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;

public interface GroupDataStore {

    public GroupData getGroupData(InMemoryGroup group);

}
