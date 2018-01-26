package be.kwakeroni.evelyn.model;

import java.util.stream.Stream;

public interface DatabaseAccessor {

    public String getSpecVersion();

    public String getDatabaseName();

    public String getAttribute(String name);

    public void createDatabase();

    public Stream<Event> getData() throws ParseException;
}
