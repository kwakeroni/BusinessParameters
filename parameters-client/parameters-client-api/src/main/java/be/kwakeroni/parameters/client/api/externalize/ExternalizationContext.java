package be.kwakeroni.parameters.client.api.externalize;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ExternalizationContext {

    <Externalizer> Externalizer getExternalizer(Class<Externalizer> type);

}
