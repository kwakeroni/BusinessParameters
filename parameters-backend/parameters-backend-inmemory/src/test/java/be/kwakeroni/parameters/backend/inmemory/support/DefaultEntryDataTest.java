package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.test.TestMap;

public class DefaultEntryDataTest extends AbstractEntryDataTest {
    @Override
    protected EntryData createEntry(String parameter, String value, String... andSoOn) {
        return DefaultEntryData.of(TestMap.of(parameter, value, andSoOn));
    }
}
