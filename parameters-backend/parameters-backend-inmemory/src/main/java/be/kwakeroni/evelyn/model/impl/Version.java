package be.kwakeroni.evelyn.model.impl;

import be.kwakeroni.evelyn.model.DatabaseAccessor;
import be.kwakeroni.evelyn.model.DatabaseException;
import be.kwakeroni.evelyn.model.DatabaseProvider;
import be.kwakeroni.evelyn.model.ParseException;
import be.kwakeroni.evelyn.storage.Storage;
import be.kwakeroni.evelyn.storage.StorageExistsException;
import be.kwakeroni.evelyn.storage.StorageProvider;

import java.util.Arrays;
import java.util.Objects;

/**
 * Specifies the data structure of an Evelyn database stream.
 * <p>
 * <p>
 * As a general rule, data must start with the following header:
 * <code>
 * !evelyn-db
 * !version=x.y
 * </code>
 * where {@code x.y} is replaced with the actual version number.
 * The remaining file must be structured according to the version as specified below.
 * </p>
 */
public enum Version implements DatabaseProvider {
    /**
     * The structure of a version 0.1 file is as follows:
     * <pre>
     *     !evelyn-db
     *     !version=0.1
     *     <i>&lt;attributes&gt;</i>
     *     !data
     *     <i>&lt;data&gt;</i>
     * </pre>
     * <p>
     * Where each line in <i>&lt;attributes&gt;</i> takes the following form:
     * <code>!<i>&lt;name&gt;</i>=<i>&lt;value&gt;</i></code>,
     * neither name or value can contain an equals-sign (=).
     * </p>
     * <p>
     * Each <i>&lt;data&gt;</i> line is preceded by either a space or a backslash, and consists of the following five fields, separated by pipes (|) :
     * <pre>&lt; |\&gt;<i>&lt;timestamp&gt;</i>|<i>&lt;object id&gt;</i>|<i>&lt;user&gt;</i>|<i>&lt;operation&gt;</i>|<i>&lt;data&gt;</i></pre>
     * <ul>
     * <li><strong>timestamp</strong>: formatted as <code>yyyyMMddHHmmss</code></li>
     * <li><strong>object id</strong>: client-specified and formatted, but not containing any pipes, backslashes or line breaks</li>
     * <li><strong>user</strong>: client-specified and formatted, but not containing any pipes, backslashes or line breaks</li>
     * <li><strong>operation</strong>: client-specified and formatted, but not containing any pipes, backslashes or line breaks</li>
     * <li><strong>data</strong>: client-specified and formatted, escaped as specified below</li>
     * </ul>
     * </p>
     * <p>
     * Escaping is done according to the following rules:
     * <ul>
     * <li>If <em>data</em> does not contain any characters that must be escaped, the line is preceded by a space and no escaping is done</li>
     * <li>If <em>data</em> contains characters to be escaped (pipes and line breaks), the line is preceded by a backslash
     * and escaping is done by preceding each pipe (|) and backslash (\) with a backslash. Line breaks (CR, LF or CRLF) are replaced by \n.
     * So:
     * <ul>
     * <li>| becomes \|</li>
     * <li>\ becomes \\</li>
     * <li>CR (\r) becomes \n</li>
     * <li>LF (\n) becomes \n</li>
     * <li>CRLF (\r\n) becomes \n</li>
     * </ul>
     * </li>
     * <li>When unescaping, \n is replaced with the system-specific line separator.</li>
     * </ul>
     * </p>
     */
    V0_1("0.1");

    public static final Version LATEST = V0_1;

    private final String number;

    Version(String number) {
        this.number = number;
    }

    public String getVersionNumber() {
        return this.number;
    }

    @Override
    public DatabaseAccessor create(StorageProvider storageProvider, String databaseName) throws DatabaseException {
        Storage storage;
        try {
            storage = storageProvider.create(databaseName);
            DefaultDatabaseAccessor accessor = new DefaultDatabaseAccessor(this.number, databaseName, storage);
            accessor.createDatabase();
            return accessor;
        } catch (StorageExistsException exc) {
            throw new DatabaseException("Could not create database " + databaseName, exc);
        }
    }

    @Override
    public DatabaseAccessor read(Storage storage) throws DatabaseException {
        try {
            return DefaultDatabaseAccessor.createFrom(storage);
        } catch (ParseException exc) {
            throw new DatabaseException(exc.getMessage(), exc);
        }
    }


    public static Version byNumber(String number) {
        Objects.requireNonNull(number, "number");
        return Arrays.stream(Version.values())
                .filter(version -> number.equals(version.getVersionNumber()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported version: " + number));
    }

}
