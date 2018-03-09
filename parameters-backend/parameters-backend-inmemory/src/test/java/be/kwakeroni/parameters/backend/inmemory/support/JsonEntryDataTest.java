package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.test.factory.TestMap;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonEntryDataTest extends AbstractEntryDataTest {

    @Override
    protected JsonEntryData createEntry(String parameter, String value, String... andSoOn) {
        JSONObject jsonObject = new JSONObject();

        TestMap.of(parameter, value, andSoOn).forEach(jsonObject::put);

        return JsonEntryData.of(jsonObject);
    }

    @Test
    @DisplayName("Provides the entry as a JSON String")
    public void testToJsonString() {
        JsonEntryData entry = createEntry("param1", "value-A", "param2", "value-B");

        assertThat(entry.toJsonString()).isEqualTo("{\"param1\":\"value-A\",\"param2\":\"value-B\"}");
    }
}