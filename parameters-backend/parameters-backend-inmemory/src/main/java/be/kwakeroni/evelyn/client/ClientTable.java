package be.kwakeroni.evelyn.client;

import java.util.stream.Stream;

public interface ClientTable<E> {

    public String getName();

    public Stream<E> findAll();

    public E findById(String id);

    public void append(String user, String operation, String objectId, String data);
}
