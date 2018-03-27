package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;
import be.kwakeroni.scratch.env.Environment;
import be.kwakeroni.scratch.env.inmemory.TransientInMemoryTestData;
import be.kwakeroni.scratch.tv.AbstractRangedTVGroup;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.MappedRangedFilterTVGroup;
import be.kwakeroni.scratch.tv.MappedRangedQueryTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedFilterTVGroup;
import be.kwakeroni.scratch.tv.RangedQueryTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;
import be.kwakeroni.scratch.tv.definition.AbstractMappedRangedTV;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by kwakeroni on 10/11/17.
 */
public class FluentTest {

    @Rule
    public Environment environment = new Environment(TransientInMemoryTestData::new);
    private BusinessParameters businessParameters;

    @Before
    public void setUp() {
        this.businessParameters = environment.getBusinessParameters();
    }


    @Test
    public void testSimpleValueQuery() {
        environment.runTestForGroup(SimpleTVGroup.instance());

        Simple simple = SimpleTVGroup.DEFINITION.createGroup(businessParameters);
        Dag dag = simple.getValue(SimpleTVGroup.DAY).orElse(null);

        assertEquals(Dag.MAANDAG, dag);
    }


    @Test
    public void testSimpleEntryQuery() {
        environment.runTestForGroup(SimpleTVGroup.instance());

        Simple simple = SimpleTVGroup.DEFINITION.createGroup(businessParameters);
        Entry entry = simple.getEntry().orElse(null);

        assertEquals(Dag.MAANDAG, entry.getValue(SimpleTVGroup.DAY));
        assertEquals(Slot.atHour(20), entry.getValue(SimpleTVGroup.SLOT));
    }

    @Test
    public void testMappedValueQuery() {
        environment.runTestForGroup(MappedTVGroup.instance());

        Mapped<Dag, Simple> mapped = MappedTVGroup.DEFINITION.createGroup(businessParameters);
        String program = mapped.forKey(Dag.ZATERDAG).getValue(MappedTVGroup.PROGRAM).get();

        assertEquals("Samson", program);
    }

    @Test
    public void testMappedEntryQuery() {
        environment.runTestForGroup(MappedTVGroup.instance());

        Mapped<Dag, Simple> mapped = MappedTVGroup.DEFINITION.createGroup(businessParameters);

        Entry entry = mapped.forKey(Dag.ZONDAG).getEntry().get();

        assertEquals(Dag.ZONDAG, entry.getValue(MappedTVGroup.DAY));
        assertEquals("Morgen Maandag", entry.getValue(MappedTVGroup.PROGRAM));
    }

    @Test
    public void testRangedValueQueryFilter() {
        testRangedValueQuery(RangedFilterTVGroup.DEFINITION);
    }

    @Test
    public void testRangedValueQueryQuery() {
        testRangedValueQuery(RangedQueryTVGroup.DEFINITION);
    }

    public void testRangedValueQuery(ParameterGroupDefinition<Ranged<Slot, Simple>> rangedDefinition) {
        environment.runTestForGroup(rangedDefinition.getName());

        String program = rangedDefinition.createGroup(businessParameters).at(Slot.atHalfPast(9)).getValue(AbstractRangedTVGroup.PROGRAM).get();

        assertEquals("Samson", program);
    }

    @Test
    public void testRangedEntryQueryFilter() {
        testRangedEntryQuery(RangedFilterTVGroup.DEFINITION);
    }

    @Test
    public void testRangedEntryQueryQuery() {
        testRangedEntryQuery(RangedQueryTVGroup.DEFINITION);
    }

    public void testRangedEntryQuery(ParameterGroupDefinition<Ranged<Slot, Simple>> rangedDefinition) {
        environment.runTestForGroup(rangedDefinition.getName());

        Entry entry = rangedDefinition.createGroup(businessParameters).at(Slot.atHour(21)).getEntry().get();

        assertEquals(Range.of(Slot.atHalfPast(20), Slot.atHour(22)), entry.getValue(AbstractRangedTVGroup.SLOT));
        assertEquals("Morgen Maandag", entry.getValue(AbstractRangedTVGroup.PROGRAM));
    }

    @Test
    public void testMappedRangedValueQueryFilter() {
        testMappedRangedValueQuery(MappedRangedFilterTVGroup.DEFINITION);
    }

    @Test
    public void testMappedRangedValueQueryQuery() {
        testMappedRangedValueQuery(MappedRangedQueryTVGroup.DEFINITION);
    }

    public void testMappedRangedValueQuery(ParameterGroupDefinition<Mapped<Dag, Ranged<Slot, Simple>>> definition) {
        environment.runTestForGroup(definition.getName());

        Mapped<Dag, Ranged<Slot, Simple>> group = definition.createGroup(businessParameters);

        assertEquals("Samson", group.forKey(Dag.ZATERDAG).at(Slot.atHalfPast(11)).getValue(AbstractMappedRangedTV.PROGRAM).get());
        assertEquals("Koers", group.forKey(Dag.ZATERDAG).at(Slot.atHour(14)).getValue(AbstractMappedRangedTV.PROGRAM).get());
        assertEquals("Morgen Maandag", group.forKey(Dag.ZONDAG).at(Slot.atHour(21)).getValue(AbstractMappedRangedTV.PROGRAM).get());
        assertEquals("Gisteren Zondag", group.forKey(Dag.MAANDAG).at(Slot.atHour(21)).getValue(AbstractMappedRangedTV.PROGRAM).get());
    }

/*
    private static final class ComposedMapped<GroupType extends EntryType, KeyType, SubType extends EntryType> implements Mapped<KeyType, SubType> {

        private final PartialQuery<GroupType, Mapped<KeyType, SubType>> partial;
        private final ParameterType<KeyType> keyType;
        private final PartialDefinition<?, GroupType, SubType> subDefinition;

        public ComposedMapped(PartialQuery<GroupType, Mapped<KeyType, SubType>> partial, ParameterType<KeyType> keyType, PartialDefinition<GroupType, SubType> subDefinition) {
            this.partial = partial;
            this.keyType = keyType;
            this.subDefinition = subDefinition;
        }

        @Override
        public SubType forKey(KeyType key) {
            PartialQuery<Mapped<KeyType, SubType>, SubType> mappedQuery = new MappedQuery.Partial<>(key, keyType);
            return subDefinition.createGroup(partial.andThen(mappedQuery));
        }
    }

    private static final class ComposedSimple<GroupType extends EntryType> implements Simple {

        private final PartialQuery<GroupType, Simple> partial;
        private final ParameterGroup<GroupType> group;
        private final BusinessParameters businessParameters;

        public ComposedSimple(PartialQuery<GroupType, Simple> partial, ParameterGroup<GroupType> group, BusinessParameters businessParameters) {
            this.partial = partial;
            this.group = group;
            this.businessParameters = businessParameters;
        }

        @Override
        public <T> Optional<T> get(Query<Simple, T> subQuery) {
            Query<GroupType, T> query = partial.andThen(subQuery);
            return businessParameters.get(group, query);
        }
    }
    */
}
