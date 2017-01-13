package be.kwakeroni.parameters.client.api.query;

import be.kwakeroni.parameters.client.api.model.EntryType;

/**
 * Created by kwakeroni on 6/05/2016.
 */
public interface Query<ET extends EntryType, Result> {

    public Object externalize(ClientWireFormatterContext context);

    public Object externalizeValue(Result result, ClientWireFormatterContext context);

    public Result internalizeResult(Object result, ClientWireFormatterContext context);

}
