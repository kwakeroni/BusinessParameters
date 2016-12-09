package be.kwakeroni.parameters.api.client.query;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ExternalizationContext {

    <Externalizer> Externalizer getExternalizer(Class<Externalizer> type);

}
