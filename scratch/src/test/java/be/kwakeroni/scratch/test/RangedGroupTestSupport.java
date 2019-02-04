package be.kwakeroni.scratch.test;

import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.scratch.EntryAssert;
import be.kwakeroni.scratch.env.Environment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class RangedGroupTestSupport<V extends Comparable<? super V>, ET extends Ranged<V, Simple>> extends GroupTestSupport<ET> {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final RangeTestParameter<V> values;
    private final ParameterType<V> rangeValueType;

    protected RangedGroupTestSupport(Environment environment,
                                     ParameterGroup<ET> group,
                                     RangeTestParameter<V> rangeParameter,
                                     TestParameter<?>... otherParameters) {
        super(environment, group, ExtArrays.prepend(rangeParameter, otherParameters));
        this.values = rangeParameter;
        this.rangeValueType = rangeParameter.rangeValueType;
    }

    protected Query<ET, ?> valueQuery(V value, int paramIndex) {
        return new RangedQuery<>(value, rangeValueType, new ValueQuery<>(parameter(paramIndex)));
    }

    protected Query<ET, Entry> entryQuery(V value) {
        return new RangedQuery<>(value, rangeValueType, new EntryQuery());
    }

    protected static <T extends Comparable<? super T>>
    RangeTestParameter<T> rangeParam(Parameter<Range<T>> parameter, ParameterType<T> rangeValueType,
                                     T lowerValue, T originalLowValue, T originalMidValue1, T originalMidValue2, T originalHighValue,
                                     T otherLowValue, T otherMidValue, T otherHighValue, T higherValue) {
        return new RangeTestParameter<T>(parameter, rangeValueType,
                lowerValue, originalLowValue, originalMidValue1, originalMidValue2, originalHighValue,
                otherLowValue, otherMidValue, otherHighValue, higherValue);
    }


    @DisplayName("Changing a parameter value")
    @Test
    public void testWriteRangedValue() {
        Query<ET, ?> query = valueQuery(this.values.originalMidValue1, 1);

        assertThat(get(query)).isEqualTo(originalValue(1));

        set(query, otherValue(1));

        assertThat(get(query)).isEqualTo(otherValue(1));
    }

    @Test
    @DisplayName("Changing an entry")
    public void testWriteRangedEntry() {
        Query<ET, Entry> query = entryQuery(this.values.originalMidValue1);

        EntryAssert.assertThat(get(query)).hasParameters(this.parameters).withValues(this.originalValues);

        set(query, entry(this.otherValues));

        assertThat(optGet(query)).isEmpty();
        EntryAssert.assertThat(get(entryQuery(this.values.otherMidValue)))
                .hasParameters(this.parameters).withValues(this.otherValues);
    }

    @Test
    @DisplayName("Adding an entry")
    public void testAddRangedEntry() {
        assertThat(optGet(entryQuery(this.values.otherMidValue))).isEmpty();

        addEntry(entry(this.otherValues));

        EntryAssert.assertThat(get(entryQuery(this.values.otherMidValue)))
                .hasParameters(this.parameters)
                .withValues(this.otherValues);
    }

    @Test
    @DisplayName("Does not allow adding an overlapping range")
    public void testAddOverlappingRange() {
        assertThat(get(valueQuery(this.values.originalMidValue1, 1))).isEqualTo(originalValue(1));
        assertThat(optGet(valueQuery(this.values.otherMidValue, 1))).isEmpty();

        assertThatThrownBy(() ->
                addEntry(entry(ExtArrays.replace(this.originalValues, 0, Range.of(this.values.lowerValue, this.values.originalMidValue2))))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining(parameter(0).getName());

        assertThatThrownBy(() ->
                addEntry(entry(ExtArrays.replace(this.originalValues, 0, Range.of(this.values.originalMidValue2, this.values.otherMidValue))))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining(parameter(0).getName());

        assertThat(get(valueQuery(this.values.originalMidValue1, 1))).isEqualTo(originalValue(1));
        assertThat(optGet(valueQuery(this.values.otherMidValue, 1))).isEmpty();
    }

    @Test
    @DisplayName("Does not allow adding a fully-contained range")
    public void testAddFullyContainedOverlappingRange() {
        assertThat(get(valueQuery(this.values.originalMidValue1, 1))).isEqualTo(originalValue(1));
        assertThat(get(valueQuery(this.values.originalMidValue2, 1))).isEqualTo(originalValue(1));

        assertThatThrownBy(() ->
                addEntry(entry(ExtArrays.replace(this.originalValues, 0, Range.of(this.values.originalMidValue1, this.values.originalMidValue2))))
        ).isInstanceOf(IllegalStateException.class)
                .satisfies(throwable -> LOG.error(throwable.toString()))
                .hasMessageContaining(parameter(0).getName());

        assertThat(get(valueQuery(this.values.originalMidValue1, 1))).isEqualTo(originalValue(1));
        assertThat(get(valueQuery(this.values.originalMidValue2, 1))).isEqualTo(originalValue(1));
    }

    @Test
    @DisplayName("Adding an entry preceding another entry")
    public void testAddAdjoiningRangeBefore() {
        assertThat(optGet(valueQuery(this.values.lowerValue, 1))).isEmpty();
        assertThat(get(valueQuery(this.values.originalMidValue1, 1))).isEqualTo(originalValue(1));

        addEntry(entry(ExtArrays.replace(this.otherValues, 0, Range.of(this.values.lowerValue, this.values.originalLowValue))));

        assertThat(get(valueQuery(this.values.originalMidValue1, 1))).isEqualTo(originalValue(1));
        assertThat(get(valueQuery(this.values.lowerValue, 1))).isEqualTo(otherValue(1));
    }

    @Test
    @DisplayName("Adding an entry following another entry")
    public void testAddAdjoiningRangeAfter() {
        assertThat(optGet(valueQuery(this.values.otherMidValue, 1))).isEmpty();
        assertThat(get(valueQuery(this.values.originalMidValue1, 1))).isEqualTo(originalValue(1));

        addEntry(entry(ExtArrays.replace(this.otherValues, 0, Range.of(this.values.originalHighValue, this.values.higherValue))));

        assertThat(get(valueQuery(this.values.originalMidValue1, 1))).isEqualTo(originalValue(1));
        assertThat(get(valueQuery(this.values.otherMidValue, 1))).isEqualTo(otherValue(1));
    }

}
