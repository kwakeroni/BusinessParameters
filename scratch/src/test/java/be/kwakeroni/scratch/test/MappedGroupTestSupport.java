package be.kwakeroni.scratch.test;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.scratch.env.Environment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.kwakeroni.scratch.EntryAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MappedGroupTestSupport<K, ET extends Mapped<K, Simple>> extends GroupTestSupport<ET> {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected MappedGroupTestSupport(Environment environment,
                                     ParameterGroup<ET> group,
                                     TestParameter<K> keyParameter,
                                     TestParameter<?>... otherParameters) {
        super(environment, group, ExtArrays.prepend(keyParameter, otherParameters));
    }

    protected Query<ET, ?> valueQuery(K key, int paramIndex) {
        return new MappedQuery<>(key, parameter(0)::toString, new ValueQuery<>(parameter(paramIndex)));
    }

    protected Query<ET, Entry> entryQuery(K key) {
        return new MappedQuery<>(key, parameter(0)::toString, new EntryQuery());
    }

    @Test
    @DisplayName("Changing a parameter value")
    void testWriteMappedValue() {
        Query<ET, ?> query = valueQuery(originalValue(0), 1);

        assertThat(get(query)).isEqualTo(originalValue(1));

        set(query, otherValue(1));

        assertThat(get(query)).isEqualTo(otherValue(1));
    }

    @Test
    @DisplayName("Changing an entry")
    public void testWriteMappedEntry() {
        Query<ET, Entry> query = entryQuery(originalValue(0));

        assertThat(get(query)).hasParameters(this.parameters).withValues(this.originalValues);

        set(query, entry(this.otherValues));

        assertThat(optGet(query)).isEmpty();
        assertThat(get(entryQuery(this.otherValue(0)))).hasParameters(this.parameters).withValues(this.otherValues);
    }

    @Test
    @DisplayName("Adding an entry")
    public void testAddMappedEntry() {
        Query<ET, Entry> query = entryQuery(originalValue(0));
        Query<ET, Entry> otherQuery = entryQuery(otherValue(0));

        assertThat(get(query)).hasParameters(this.parameters).withValues(this.originalValues);
        assertThat(optGet(otherQuery)).isEmpty();

        addEntry(entry(this.otherValues));

        assertThat(get(query)).hasParameters(this.parameters).withValues(this.originalValues);
        assertThat(get(otherQuery)).hasParameters(this.parameters).withValues(this.otherValues);
    }

    @Test
    @DisplayName("Does not allow adding an incomplete entry")
    public void testAddIncompleteEntry() {

        Query<ET, Entry> otherQuery = entryQuery(otherValue(0));

        assertThat(optGet(otherQuery)).isEmpty();

        Entry entry = entry(ExtArrays.select(parameters, 0, parameters.length - 1),
                ExtArrays.select(otherValues, 0, parameters.length - 1));

        assertThatThrownBy(() -> addEntry(entry))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(parameter(parameters.length - 1).getName());

        assertThat(optGet(otherQuery)).isEmpty();
    }

    @Test
    @DisplayName("Does not allow adding a duplicate key")
    public void testAddDuplicateMapKey() {
        Query<ET, ?> query = valueQuery(this.originalValue(0), 1);

        assertThat(get(query)).isEqualTo(this.originalValue(1));

        Entry entry = entry(ExtArrays.replace(originalValues, 1, otherValue(1)));

        assertThatThrownBy(() -> addEntry(entry))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(parameter(0).getName())
                .hasMessageContaining(originalValue(0).toString());

        assertThat(get(query)).isEqualTo(this.originalValue(1));

    }

    @Test
    @DisplayName("Does not allow modifying a value to have a duplicate key")
    public void testModifyValueToDuplicateMapKey() {
        Query<ET, ?> query = valueQuery(this.originalValue(0), 1);
        Query<ET, ?> otherQuery = valueQuery(this.otherValue(0), 1);


        addEntry(entry(this.otherValues));

        assertThat(get(query)).isEqualTo(this.originalValue(1));
        assertThat(get(otherQuery)).isEqualTo(this.otherValue(1));

        assertThatThrownBy(() -> set(valueQuery(this.otherValue(0), 0), this.originalValue(0)))
                .isInstanceOf(IllegalStateException.class)
                .satisfies(System.out::println)
                .hasMessageContaining(parameter(0).getName())
                .hasMessageContaining(originalValue(0).toString());

        assertThat(get(query)).isEqualTo(this.originalValue(1));
        assertThat(get(otherQuery)).isEqualTo(this.otherValue(1));
    }


    @Test
    @DisplayName("Does not allow modifying an entry to have a duplicate key")
    public void testModifyEntryToDuplicateMapKey() {
        Query<ET, ?> query = valueQuery(this.originalValue(0), 1);
        Query<ET, ?> otherQuery = valueQuery(this.otherValue(0), 1);

        addEntry(entry(this.otherValues));

        assertThat(get(query)).isEqualTo(this.originalValue(1));
        assertThat(get(otherQuery)).isEqualTo(this.otherValue(1));

        Entry entry = entry(ExtArrays.replace(otherValues, 0, originalValue(0)));

        assertThatThrownBy(() -> set(entryQuery(this.otherValue(0)), entry))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(parameter(0).getName())
                .hasMessageContaining(originalValue(0).toString());

        assertThat(get(query)).isEqualTo(this.originalValue(1));
        assertThat(get(otherQuery)).isEqualTo(this.otherValue(1));

    }
}
