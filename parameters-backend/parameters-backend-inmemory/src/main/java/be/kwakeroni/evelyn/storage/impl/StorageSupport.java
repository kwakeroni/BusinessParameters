package be.kwakeroni.evelyn.storage.impl;

import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;

import java.nio.charset.Charset;

abstract class StorageSupport implements Storage {

    protected abstract void initialize() throws StorageExistsException;

    @Override
    public void writeHeader(String version) throws StorageExistsException {
        initialize();
        HeaderStructure.writeHeader(version, this::append);
    }

    @Override
    public String readVersion() throws ParseException {
        return HeaderStructure.readVersion(() -> this.read(Charset.defaultCharset()));
    }

}
