package be.kwakeroni.evelyn.storage;

import be.kwakeroni.evelyn.model.ParseException;

import java.nio.charset.Charset;
import java.util.stream.Stream;

public interface Storage {

    public String getReference();

    public void writeHeader(String version) throws StorageExistsException;

    public void append(String data);

    public String readVersion() throws ParseException;

    public Stream<String> read(Charset charset);

}
