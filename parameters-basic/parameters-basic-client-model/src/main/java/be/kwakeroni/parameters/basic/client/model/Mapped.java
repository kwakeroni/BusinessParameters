package be.kwakeroni.parameters.basic.client.model;

import be.kwakeroni.parameters.api.client.model.EntryType;

/**
 * Created by kwakeroni on 6/05/2016.
 */
public interface Mapped<KeyType, ET extends EntryType> extends EntryType {
    ET forKey(KeyType key);
}
