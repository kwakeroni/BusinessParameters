package be.kwakeroni.scratch.test;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.scratch.EntryAssert;
import be.kwakeroni.scratch.env.Environment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class SimpleGroupTestSupport extends GroupTestSupport<Simple> {

    protected SimpleGroupTestSupport(Environment environment, ParameterGroup<Simple> group, TestParameter<?>... parameters) {
        super(environment, group, parameters);
    }

    protected Query<Simple, ?> valueQuery(int paramIndex) {
        return new ValueQuery<>(parameter(paramIndex));
    }

    protected Query<Simple, Entry> entryQuery() {
        return new EntryQuery();
    }

    @Test
    @DisplayName("Changing a parameter value")
    public void testWriteSimpleValue() {
        Query<Simple, ?> query = valueQuery(0);
        assertThat(get(query)).isEqualTo(originalValue(0));

        set(query, otherValue(0));

        assertThat(get(query)).isEqualTo(otherValues[0]);
    }

    @Test
    @DisplayName("Changing an entry")
    public void testWriteSimpleEntry() {
        Query<Simple, Entry> query = entryQuery();

        EntryAssert.assertThat(get(query))
                .hasParameters(parameters)
                .withValues(originalValues);

        set(query, entry(otherValues));

        EntryAssert.assertThat(get(query))
                .hasParameters(parameters)
                .withValues(otherValues);
    }

    @Test
    @DisplayName("Does not allow null values")
    public void testWriteNullValue() {
        Query<Simple, ?> query = valueQuery(0);
        assertThat(get(query)).isEqualTo(originalValue(0));

        assertThatThrownBy(() ->
                set(query, null)
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(get(query)).isEqualTo(originalValue(0));
    }

    @Test
    @DisplayName("Does not allow adding entries")
    public void testAddEntry() {
        Query<Simple, ?> query = valueQuery(0);

        assertThat(get(query)).isEqualTo(originalValue(0));

        assertThatThrownBy(() ->
                addEntry(entry(this.otherValues))
        ).isInstanceOf(IllegalStateException.class);

        assertThat(get(query)).isEqualTo(originalValue(0));
    }

}
