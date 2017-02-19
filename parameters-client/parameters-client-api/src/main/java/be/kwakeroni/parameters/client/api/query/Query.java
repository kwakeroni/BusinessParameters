package be.kwakeroni.parameters.client.api.query;

import be.kwakeroni.parameters.client.api.model.EntryType;

import java.util.Optional;

/**
 * Created by kwakeroni on 6/05/2016.
 */
public interface Query<ET extends EntryType, Result> {

    public Object externalize(ClientWireFormatterContext context);

    public Object externalizeValue(Result value, ClientWireFormatterContext context);

    public Optional<Result> internalizeResult(Object result, ClientWireFormatterContext context);

}
