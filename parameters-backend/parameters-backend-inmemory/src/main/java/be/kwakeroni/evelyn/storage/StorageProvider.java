package be.kwakeroni.evelyn.storage;

public interface StorageProvider {
    public Storage create(String name) throws StorageExistsException;

    public Storage read(String name);

    public boolean exists(String name);
}
