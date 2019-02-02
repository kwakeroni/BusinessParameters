package be.kwakeroni.scratch;

import be.kwakeroni.parameters.basic.client.model.Historicized;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.types.support.ParameterTypes;
import be.kwakeroni.scratch.env.Environment;
import be.kwakeroni.scratch.env.es.ElasticSearchTestData;
import be.kwakeroni.scratch.env.inmemory.PersistedInMemoryTestData;
import be.kwakeroni.scratch.env.inmemory.TransientInMemoryTestData;
import be.kwakeroni.scratch.test.EnvironmentTestSupport;
import be.kwakeroni.scratch.test.MappedGroupTestSupport;
import be.kwakeroni.scratch.test.MappedRangedGroupTestSupport;
import be.kwakeroni.scratch.test.RangedGroupTestSupport;
import be.kwakeroni.scratch.test.SimpleGroupTestSupport;
import be.kwakeroni.scratch.tv.AbstractMappedRangedTVGroup;
import be.kwakeroni.scratch.tv.AbstractRangedTVGroup;
import be.kwakeroni.scratch.tv.Dag;
import be.kwakeroni.scratch.tv.HistoricizedTVGroup;
import be.kwakeroni.scratch.tv.MappedRangedFilterTVGroup;
import be.kwakeroni.scratch.tv.MappedRangedQueryTVGroup;
import be.kwakeroni.scratch.tv.MappedTVGroup;
import be.kwakeroni.scratch.tv.RangedFilterTVGroup;
import be.kwakeroni.scratch.tv.RangedQueryTVGroup;
import be.kwakeroni.scratch.tv.SimpleTVGroup;
import be.kwakeroni.scratch.tv.Slot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;

public class WriteTest {

    @Nested
    @DisplayName("Using Transient in-memory data")
    class TransientInMemoryWriteTest extends AbstractWriteTVTest {
        public TransientInMemoryWriteTest() {
            super(new Environment(TransientInMemoryTestData::new));
        }

    }

    @Nested
    @DisplayName("Using Persisted in-memory data")
    class PersistedInMemoryWriteTest extends AbstractWriteTVTest {
        public PersistedInMemoryWriteTest() {
            super(new Environment(PersistedInMemoryTestData::new));
        }
    }

    @Nested
    @DisplayName("Using ElasticSearch data")
    class ESInMemoryWriteTest extends AbstractWriteTVTest {
        public ESInMemoryWriteTest() {
            super(new Environment(ElasticSearchTestData::new));
        }
    }

    public abstract class AbstractWriteTVTest extends EnvironmentTestSupport {

        public AbstractWriteTVTest(Environment environment) {
            super(environment);
        }

        @Nested
        @DisplayName("Modifies Simple Groups")
        class SimpleGroupTest extends SimpleGroupTestSupport {
            public SimpleGroupTest() {
                super(environment, SimpleTVGroup.instance(),
                        param(SimpleTVGroup.DAY, Dag.MAANDAG, Dag.DONDERDAG),
                        param(SimpleTVGroup.SLOT, Slot.atHour(20), Slot.atHalfPast(18)));
            }
        }

        @Nested
        @DisplayName("Modifies Mapped Groups")
        class MappedGroupTest extends MappedGroupTestSupport<Dag, Mapped<Dag, Simple>> {
            public MappedGroupTest() {
                super(environment,
                        MappedTVGroup.instance(),
                        param(MappedTVGroup.DAY, Dag.ZONDAG, Dag.DONDERDAG),
                        param(MappedTVGroup.PROGRAM, "Morgen Maandag", "Alles Kan Beter"));
            }
        }

        @Nested
        @DisplayName("Modifies Historicized Groups")
        class HistoricizedGroupTest extends RangedGroupTestSupport<LocalDate, Historicized<Simple>> {
            public HistoricizedGroupTest() {
                super(environment, HistoricizedTVGroup.instance(),
                        rangeParam(HistoricizedTVGroup.PERIOD, ParameterTypes.LOCAL_DATE,
                                LocalDate.of(2018, 12, 1),
                                LocalDate.of(2018, 12, 15), LocalDate.of(2019, 2, 1), LocalDate.of(2019, 3, 1), LocalDate.of(2019, 3, 15),
                                LocalDate.of(2019, 4, 1), LocalDate.of(2019, 5, 15), LocalDate.of(2019, 6, 1),
                                LocalDate.of(2019, 6, 15)),
                        param(HistoricizedTVGroup.PROGRAM, "Winter Wonderland", "Zondag Josdag")

                );
            }
        }


        @Nested
        @DisplayName("Modifies Ranged-Filter Groups")
        class RangedFilterGroupTest extends RangedGroupTestSupport<Slot, Ranged<Slot, Simple>> {
            public RangedFilterGroupTest() {
                super(environment, RangedFilterTVGroup.instance(),
                        rangeParam(AbstractRangedTVGroup.SLOT, Slot.type,
                                Slot.atHour(7), Slot.atHour(8), Slot.atHalfPast(9), Slot.atHour(11), Slot.atHour(12),
                                Slot.atHour(13), Slot.atHour(13), Slot.atHour(14), Slot.atHour(15)),
                        param(AbstractRangedTVGroup.PROGRAM, "Samson", "TikTak"));
            }
        }


        @Nested
        @DisplayName("Modifies Ranged-Query Groups")
        class RangedQueryGroupTest extends RangedGroupTestSupport<Slot, Ranged<Slot, Simple>> {
            public RangedQueryGroupTest() {
                super(environment, RangedQueryTVGroup.instance(),
                        rangeParam(AbstractRangedTVGroup.SLOT, Slot.type,
                                Slot.atHour(7), Slot.atHour(8), Slot.atHalfPast(9), Slot.atHour(11), Slot.atHour(12),
                                Slot.atHour(13), Slot.atHour(13), Slot.atHour(14), Slot.atHour(15)),
                        param(AbstractRangedTVGroup.PROGRAM, "Samson", "TikTak"));
            }
        }

        @Nested
        @DisplayName("Modifies Mapped Ranged-Filter Groups")
        class MappedRangedFilterGroupTest extends MappedRangedGroupTestSupport<Dag, Slot, Mapped<Dag, Ranged<Slot, Simple>>> {
            public MappedRangedFilterGroupTest() {
                super(environment, MappedRangedFilterTVGroup.instance(),
                        param(AbstractMappedRangedTVGroup.DAY, Dag.ZATERDAG, Dag.DINSDAG),
                        rangeParam(AbstractMappedRangedTVGroup.SLOT, Slot.type,
                                Slot.atHalfPast(7), Slot.atHour(8), Slot.atHour(10), Slot.atHour(11), Slot.atHour(12),
                                Slot.atHour(18), Slot.atHour(19), Slot.atHour(20), Slot.atHalfPast(21)),
                        param(AbstractMappedRangedTVGroup.PROGRAM, "Samson", "TikTak"));

            }
        }

        @Nested
        @DisplayName("Modifies Mapped Ranged-Query Groups")
        class MappedRangedQueryGroupTest extends MappedRangedGroupTestSupport<Dag, Slot, Mapped<Dag, Ranged<Slot, Simple>>> {
            public MappedRangedQueryGroupTest() {
                super(environment, MappedRangedQueryTVGroup.instance(),
                        param(AbstractMappedRangedTVGroup.DAY, Dag.ZATERDAG, Dag.DINSDAG),
                        rangeParam(AbstractMappedRangedTVGroup.SLOT, Slot.type,
                                Slot.atHalfPast(7), Slot.atHour(8), Slot.atHour(10), Slot.atHour(11), Slot.atHour(12),
                                Slot.atHour(18), Slot.atHour(19), Slot.atHour(20), Slot.atHalfPast(21)),
                        param(AbstractMappedRangedTVGroup.PROGRAM, "Samson", "TikTak"));

            }
        }

    }


}
