package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;
import be.kwakeroni.parameters.backend.inmemory.support.DefaultEntryData;
import be.kwakeroni.parameters.basic.backend.es.ElasticSearchHistoricizedGroup;
import be.kwakeroni.parameters.basic.client.support.Entries;
import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.basic.type.Ranges;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.scratch.tv.definition.HistoricizedTV;
import be.kwakeroni.test.factory.TestMap;

import java.time.LocalDate;
import java.util.Map;

public class HistoricizedTVGroup extends HistoricizedTV {

    public static final HistoricizedTVGroup instance() {
        return new HistoricizedTVGroup();
    }

    public static Entry entry(LocalDate from, LocalDate to, String program) {
        return Entries.entryOf(PERIOD, Range.of(from, to), PROGRAM, program);
    }

    public static EntryData entryData(String from, String to, String program) {
        return DefaultEntryData.of(TestMap.of(
                PERIOD.getName(), Ranges.toRangeString(from, to),
                PROGRAM.getName(), program
        ));
    }

    public static Map<String, ?> enrichedEntryData(String from, String to, String program) {
        return TestMap.of(
                PERIOD.getName(), Ranges.toRangeString(from, to),
                ElasticSearchHistoricizedGroup.getFromParameter(PERIOD.getName()), toJsonFormat(from),
                ElasticSearchHistoricizedGroup.getToParameter(PERIOD.getName()), toJsonFormat(to),
                PROGRAM.getName(), program
        );
    }

    private static String toJsonFormat(String original) {
        if (!original.matches("\\d{8}")) {
            throw new IllegalArgumentException("Unexpected date: " + original);
        }
        return original.substring(0, 4) + "-" + original.substring(4, 6) + "-" + original.substring(6, 8);
    }

}
