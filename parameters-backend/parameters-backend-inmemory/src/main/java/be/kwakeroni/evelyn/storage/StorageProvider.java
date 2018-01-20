package be.kwakeroni.evelyn.storage;

public interface StorageProvider {

    public Storage create(String name) throws StorageExistsException;

}
