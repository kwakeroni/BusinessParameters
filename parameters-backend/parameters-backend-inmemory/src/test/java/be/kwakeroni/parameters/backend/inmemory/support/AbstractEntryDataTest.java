package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

abstract class AbstractEntryDataTest {

    protected abstract EntryData createEntry(String parameter, String value, String... andSoOn);

    @Test
    @DisplayName("Provides an id")
    public void testGetId() {
        EntryData entry = createEntry("param", "value");
        assertThat(entry.getId()).isNotNull();
    }

    @Test
    @DisplayName("Provides parameter values in a map")
    public void testGetParameters() {
        EntryData entry = createEntry("param1", "value-A", "param2", "value-B");

        assertThat(entry.getParameters())
                .containsOnly(entry("param1", "value-A"), entry("param2", "value-B"));
    }

    @Test
    @DisplayName("Provides the entry as a map")
    public void testAsMap() {
        EntryData entry = createEntry("param1", "value-A", "param2", "value-B");

        assertThat(entry.asMap())
                .containsOnly(entry("param1", "value-A"), entry("param2", "value-B"));
    }


    @Test
    @DisplayName("Gets the value of a parameter")
    public void testGetValue() {
        EntryData entry = createEntry("param1", "value-A", "param2", "value-B");

        assertThat(entry.getValue("param1")).isEqualTo("value-A");
        assertThat(entry.getValue("param2")).isEqualTo("value-B");
    }

    @Test
    @DisplayName("Sets the value of a parameter")
    public void testSetValue() {
        EntryData entry = createEntry("param1", "value-A", "param2", "value-B");

        assertThat(entry.getValue("param2")).isEqualTo("value-B");

        entry.setValue("param2", "value-C");

        assertThat(entry.getValue("param1")).isEqualTo("value-A");
        assertThat(entry.getValue("param2")).isEqualTo("value-C");
    }

    @Test
    @DisplayName("Fails when setting the value of an unknown parameter")
    public void testSetValueOfUnknown() {
        EntryData entry = createEntry("param1", "value-A", "param2", "value-B");

        assertThatThrownBy(() -> entry.setValue("param3", "value-C"))
                .isInstanceOf(IllegalArgumentException.class);


    }


}
