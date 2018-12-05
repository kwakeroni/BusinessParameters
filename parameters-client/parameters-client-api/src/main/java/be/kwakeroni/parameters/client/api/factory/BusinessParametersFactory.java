package be.kwakeroni.parameters.client.api.factory;

import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.WritableBusinessParameters;

import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BusinessParametersFactory {

    public BusinessParameters getInstance(Map<String, String> properties);

    public WritableBusinessParameters getWritableInstance(Map<String, String> properties);

}
