package be.kwakeroni.scratch;

import be.kwakeroni.parameters.api.client.BusinessParameters;
import be.kwakeroni.parameters.api.client.factory.BusinessParametersFactory;
import be.kwakeroni.parameters.backend.inmemory.factory.InMemoryBackendServiceFactory;
import be.kwakeroni.parameters.backend.inmemory.service.InMemoryBackend;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class Environment {


    public static void main(String[] args){
        new Environment();
    }

    InMemoryBackend backend;
    BusinessParameters parameters;

    public Environment(){
        this.backend = InMemoryBackendServiceFactory.getSingletonInstance();
        initData();
        BusinessParametersFactory factory = load(BusinessParametersFactory.class);
        this.parameters = factory.getInstance();
    }

    public BusinessParameters getBusinessParameters(){
        return this.parameters;
    }

    private <S> S load(Class<S> serviceType){
        ServiceLoader<S> loader = ServiceLoader.load(serviceType);
        Iterator<S> services = loader.iterator();
        if (! services.hasNext()){
            throw new IllegalStateException("Service not found: " + serviceType.getName());
        }
        S service = services.next();
        if (services.hasNext()){
            throw new IllegalStateException("Multiple services of type " + serviceType.getName() + ": " + service.getClass().getName() + " & " + services.next().getClass().getName());
        }
        return service;
    }

    private void initData(){
        this.backend.addGroupData(SimpleTVGroup.instance().getName(), SimpleTVGroup.getData(Dag.MAANDAG, Slot.atHour(20)));
    }
}
