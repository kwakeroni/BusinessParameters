package be.kwakeroni.evelyn.model;

public interface DatabaseAccessor {

    public String getSpecVersion();

    public String getDatabaseName();

    public String getAttribute(String name);

    public void createDatabase();

}
