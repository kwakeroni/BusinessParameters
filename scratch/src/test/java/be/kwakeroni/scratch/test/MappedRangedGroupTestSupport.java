package be.kwakeroni.scratch.test;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.EntryQuery;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.scratch.env.Environment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class MappedRangedGroupTestSupport<K, V extends Comparable<? super V>, ET extends Mapped<K, Ranged<V, Simple>>>
        extends GroupTestSupport<ET> {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final RangeTestParameter<V> rangePoints;
    private final ParameterType<V> rangeValueType;

    protected MappedRangedGroupTestSupport(Environment environment,
                                           ParameterGroup<ET> group,
                                           TestParameter<K> keyParameter,
                                           RangeTestParameter<V> rangeParameter,
                                           TestParameter<?>... otherParameters) {
        super(environment, group, ExtArrays.prepend(keyParameter, rangeParameter, otherParameters));
        this.rangePoints = rangeParameter;
        this.rangeValueType = rangeParameter.rangeValueType;
    }

    protected <P> Query<ET, P> valueQuery(K key, V rangePoint, int paramIndex) {
        return new MappedQuery<>(key, parameter(0)::toString,
                new RangedQuery<>(rangePoint, rangeValueType,
                        new ValueQuery<>(parameter(paramIndex))));
    }

    protected Query<ET, Entry> entryQuery(K key, V rangePoint) {
        return new MappedQuery<>(key, parameter(0)::toString,
                new RangedQuery<>(rangePoint, rangeValueType,
                        new EntryQuery()));
    }

    protected static <T extends Comparable<? super T>>
    RangeTestParameter<T> rangeParam(Parameter<Range<T>> parameter, ParameterType<T> rangeValueType,
                                     T lowerValue, T originalLowValue, T originalMidValue1, T originalMidValue2, T originalHighValue,
                                     T otherLowValue, T otherMidValue, T otherHighValue, T higherValue) {
        return new RangeTestParameter<T>(parameter, rangeValueType,
                lowerValue, originalLowValue, originalMidValue1, originalMidValue2, originalHighValue,
                otherLowValue, otherMidValue, otherHighValue, higherValue);
    }

    @Test
    @DisplayName("Adding an entry below another map key")
    public void testAddEntryForRangeInAnotherMapKey() {
        Query<ET, ?> query = valueQuery(originalValue(0), this.rangePoints.originalMidValue1, 2);
        Query<ET, ?> otherQuery = valueQuery(otherValue(0), this.rangePoints.originalMidValue1, 2);

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(optGet(otherQuery)).isEmpty();

        Entry entry = entry(ExtArrays.replace(this.otherValues, 1, originalValue(1)));
        addEntry(entry);

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(get(otherQuery)).isEqualTo(otherValue(2));
    }

    @Test
    @DisplayName("Adding an entry for another range below the same map key")
    public void testAddEntryForOtherRangeInSameMapKey() {
        Query<ET, ?> query = valueQuery(originalValue(0), this.rangePoints.originalMidValue1, 2);
        Query<ET, ?> otherQuery = valueQuery(originalValue(0), this.rangePoints.otherMidValue, 2);

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(optGet(otherQuery)).isEmpty();

        Entry entry = entry(ExtArrays.replace(this.otherValues, 0, originalValue(0)));
        addEntry(entry);

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(get(otherQuery)).isEqualTo(otherValue(2));
    }

    @Test
    @DisplayName("Does not allow to modify a range to overlap")
    public void testModifyRangeToOverlappingRange() {
        Object[] localValues = ExtArrays.replace(otherValues, 0, originalValue(0));
        addEntry(entry(localValues));

        Query<ET, ?> query = valueQuery(originalValue(0), this.rangePoints.originalMidValue1, 2);
        Query<ET, ?> otherQuery = valueQuery(originalValue(0), this.rangePoints.otherMidValue, 2);
        Query<ET, Range<?>> rangeQuery = valueQuery(originalValue(0), this.rangePoints.otherMidValue, 1);

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(get(otherQuery)).isEqualTo(otherValue(2));


        assertThatThrownBy(() ->
                set(rangeQuery, Range.of(this.rangePoints.originalMidValue2, this.rangePoints.otherMidValue))
        ).isInstanceOf(IllegalStateException.class);

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(get(otherQuery)).isEqualTo(otherValue(2));
    }

    @Test
    @DisplayName("Changes the key of an entry")
    public void testModifyKeyToNonOverlappingRange() {
        addEntry(entry(otherValues));

        Query<ET, ?> query = valueQuery(originalValue(0), this.rangePoints.originalMidValue1, 2);
        Query<ET, ?> otherQuery = valueQuery(otherValue(0), this.rangePoints.otherMidValue, 2);
        Query<ET, ?> keyQuery = valueQuery(otherValue(0), this.rangePoints.otherMidValue, 0);
        Query<ET, ?> newQuery = valueQuery(originalValue(0), this.rangePoints.otherMidValue, 2);

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(get(otherQuery)).isEqualTo(otherValue(2));

        set(keyQuery, originalValue(0));

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(get(newQuery)).isEqualTo(otherValue(2));
    }

    @Test
    @DisplayName("Does not allow to modify a key causing an overlapping range")
    public void testModifyKeyToOverlappingRange() {
        Object[] localValues = ExtArrays.replace(otherValues, 1, originalValue(1));
        addEntry(entry(localValues));

        Query<ET, ?> query = valueQuery(originalValue(0), this.rangePoints.originalMidValue1, 2);
        Query<ET, ?> otherQuery = valueQuery(otherValue(0), this.rangePoints.originalMidValue1, 2);
        Query<ET, ?> keyQuery = valueQuery(otherValue(0), this.rangePoints.originalMidValue1, 0);

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(get(otherQuery)).isEqualTo(otherValue(2));

        assertThatThrownBy(() ->
                set(keyQuery, originalValue(0))
        ).isInstanceOf(IllegalStateException.class);

        assertThat(get(query)).isEqualTo(originalValue(2));
        assertThat(get(otherQuery)).isEqualTo(otherValue(2));
    }
}
