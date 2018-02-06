package be.kwakeroni.evelyn.model;

import be.kwakeroni.evelyn.storage.StorageExistsException;

import java.util.stream.Stream;

public interface DatabaseAccessor {

    public String getSpecVersion();

    public String getDatabaseName();

    public String getAttribute(String name);

    public void createDatabase() throws StorageExistsException;

    public Stream<Event> getData() throws ParseException;
}
