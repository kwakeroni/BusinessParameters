package be.kwakeroni.parameters.client.model.basic;

import be.kwakeroni.parameters.client.model.EntryType;

/**
 * Created by kwakeroni on 6/05/2016.
 */
public interface Ranged<V, ET extends EntryType> extends EntryType {

    public ET at(V value);

}
