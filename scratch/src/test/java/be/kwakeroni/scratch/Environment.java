package be.kwakeroni.scratch;

import be.kwakeroni.parameters.api.client.BusinessParameters;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class Environment {


    public static void main(String[] args){
        new Environment().init();
    }

    public void init(){
        BusinessParameters parameters = load(BusinessParameters.class);
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

}
