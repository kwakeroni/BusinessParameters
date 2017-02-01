package be.kwakeroni.parameters.client.api.factory;

import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BusinessParametersFactory {

    public BusinessParameters getInstance();

    public WritableBusinessParameters getWritableInstance();

}
