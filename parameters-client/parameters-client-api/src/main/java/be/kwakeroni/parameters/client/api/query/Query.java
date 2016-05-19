package be.kwakeroni.parameters.client.api.query;

import be.kwakeroni.parameters.client.api.externalize.ExternalizationContext;
import be.kwakeroni.parameters.client.model.EntryType;

/**
 * Created by kwakeroni on 6/05/2016.
 */
public interface Query<ET extends EntryType, Result> {

    Object externalize(ExternalizationContext context);

}
