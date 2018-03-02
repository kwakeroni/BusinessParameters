package be.kwakeroni.parameters.backend.inmemory.persistence;

import be.kwakeroni.evelyn.client.ClientTable;
import be.kwakeroni.evelyn.client.DefaultClientTable;
import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;

import java.util.function.Function;

public final class GroupTableFactory {

    private final Function<String, DatabaseAccessor> accessorSupplier;

    public GroupTableFactory(Function<String, DatabaseAccessor> accessorSupplier) {
        this.accessorSupplier = accessorSupplier;
    }

    public ClientTable<EntryData> createTable(InMemoryGroup group) {
        return new DefaultClientTable<>(accessorSupplier.apply(group.getName()), GroupTableOperation::valueOf);
    }

}
