package be.kwakeroni.parameters.api.client.query;

import be.kwakeroni.parameters.api.client.model.EntryType;

/**
 * Created by kwakeroni on 6/05/2016.
 */
public interface Query<ET extends EntryType, Result> {

    Object externalize(ClientWireFormatterContext context);

    Result internalizeResult(Object result, ClientWireFormatterContext context);

}
