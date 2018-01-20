package be.kwakeroni.evelyn.storage;

import java.util.stream.Stream;

public interface Storage {

    public void writeHeader(String version);

    public void append(String data);

    public String readVersion();

    public Stream<String> read();


}
