package be.kwakeroni.scratch;

import be.kwakeroni.parameters.backend.inmemory.factory.InMemoryBackendServiceFactory;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;
import be.kwakeroni.parameters.client.api.factory.BusinessParametersFactory;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.MappedRangedTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class Environment {


    public static void main(String[] args) {
        new Environment();
    }

    InMemoryBackend backend;
    WritableBusinessParameters parameters;

    public Environment() {
        this.backend = InMemoryBackendServiceFactory.getSingletonInstance();
        initData();
        BusinessParametersFactory factory = load(BusinessParametersFactory.class);
        this.parameters = factory.getWritableInstance();
    }

    public BusinessParameters getBusinessParameters() {
        return this.parameters;
    }

    public WritableBusinessParameters getWritableBusinessParameters() {
        return this.parameters;
    }

    private <S> S load(Class<S> serviceType) {
        ServiceLoader<S> loader = ServiceLoader.load(serviceType);
        Iterator<S> services = loader.iterator();
        if (!services.hasNext()) {
            throw new IllegalStateException("Service not found: " + serviceType.getName());
        }
        S service = services.next();
        if (services.hasNext()) {
            throw new IllegalStateException("Multiple services of type " + serviceType.getName() + ": " + service.getClass().getName() + " & " + services.next().getClass().getName());
        }
        return service;
    }

    private void initData() {
        this.backend.setGroupData(SimpleTVGroup.instance().getName(),
                SimpleTVGroup.getData(Dag.MAANDAG, Slot.atHour(20)));
        this.backend.setGroupData(MappedTVGroup.instance().getName(),
                MappedTVGroup.getData(Dag.ZATERDAG, "Samson",
                        Dag.ZONDAG, "Morgen Maandag"));
        this.backend.setGroupData(RangedTVGroup.instance().getName(),
                RangedTVGroup.getData(Slot.atHour(8), Slot.atHour(12), "Samson",
                        Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag"));
        this.backend.setGroupData(MappedRangedTVGroup.instance().getName(),
                MappedRangedTVGroup.getData(
                        MappedRangedTVGroup.entryData(Dag.MAANDAG, Slot.atHalfPast(20), Slot.atHour(22), "Gisteren Zondag"),
                        MappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(8), Slot.atHour(12), "Samson"),
                        MappedRangedTVGroup.entryData(Dag.ZATERDAG, Slot.atHour(14), Slot.atHour(18), "Koers"),
                        MappedRangedTVGroup.entryData(Dag.ZONDAG, Slot.atHalfPast(20), Slot.atHour(22), "Morgen Maandag")
                ));

    }
}
