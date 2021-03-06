package be.kwakeroni.parameters_exp.basic.client.model;

import be.kwakeroni.parameters.client.api.BusinessParameterGroup;
import be.kwakeroni.parameters.client.api.model.EntryType;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters_exp.basic.client.query.BasicQueries;
import be.kwakeroni.parameters_exp.client.api.entry.EntryTypeBuilder;

import java.util.Optional;
import java.util.function.Function;

import static be.kwakeroni.parameters_exp.basic.client.query.BasicQueries.entryAt;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class BasicEntryTypes {

    private BasicEntryTypes() {

    }

    public static <Target extends EntryType, Value, Missing extends EntryType>
    EntryTypeBuilder<Target, Ranged<Value, Missing>> ranged(Function<Value, String> type, EntryTypeBuilder<Target, Missing> retrieveEntry) {
        return (findingParentEntry) -> new Ranged<Value, Missing>() {
            //            @Override
            public Missing at(Value value) {
                return retrieveEntry.by(findingParentEntry.andThen(entryAt(value, type)));
            }

            public String toString() {
                return retrieveEntry + " by " + findingParentEntry + " and then finding entryAt(<value>)";
            }
        };
    }

    public static <Target extends EntryType, K, Missing extends EntryType>
    EntryTypeBuilder<Target, Mapped<K, Missing>> mapped(Function<K, String> keyType, EntryTypeBuilder<Target, Missing> retrieveEntry) {
        return (findingParentEntry) -> new Mapped<K, Missing>() {
            //            @Override
            public Missing forKey(K key) {
                return retrieveEntry.by(findingParentEntry.andThen(BasicQueries.forKey(key, keyType)));
            }

            public String toString() {
                return findingParentEntry + " and then finding entry forKey(<key>)";
            }

        };
    }

    public static <Target extends EntryType> EntryTypeBuilder<Target, Simple> in(BusinessParameterGroup<Target> group) {
        return (findingEntry) -> new Simple() {
            //            @Override
            public <T> Optional<T> get(Query<Simple, T> findResult) {
                return group.get(findingEntry.andThen(findResult));
            }

            public String toString() {
                return findingEntry + " in " + group.getName() + " and then <find result>";
            }
        };
    }

}
