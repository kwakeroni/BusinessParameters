package be.kwakeroni.parameters.basic.definition.inmemory;

import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroup;
import be.kwakeroni.parameters.backend.inmemory.api.InMemoryGroupFactory;
import be.kwakeroni.parameters.basic.backend.inmemory.InmemoryHistoricizedGroup;
import be.kwakeroni.parameters.basic.definition.factory.HistoricizedDefinitionVisitor;

import java.util.function.Consumer;

public class InMemoryHistoricizedGroupFactory implements HistoricizedDefinitionVisitor<InMemoryGroup>, InMemoryGroupFactory {

    @Override
    public void register(Registry registry) {
        registry.register(HistoricizedDefinitionVisitor.class, this);
    }

    @Override
    public void unregister(Consumer<Class<?>> registry) {
        registry.accept(HistoricizedDefinitionVisitor.class);
    }

    @Override
    public InMemoryGroup visit(Definition definition, InMemoryGroup subGroup) {
        return new InmemoryHistoricizedGroup(definition.getPeriodParameter(), definition.getDefinition(), subGroup);
    }
}
