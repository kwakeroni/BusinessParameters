package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.model.EntryType;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
@FunctionalInterface
public interface QuerySupplier<DownType extends EntryType, UpType extends EntryType> {

    public <T> Query<UpType, T> andThen(Query<DownType, T> subQuery);

    public default <BottomType extends EntryType> QuerySupplier<BottomType, UpType> andThen(QuerySupplier<BottomType, DownType> subQuery){
        QuerySupplier<DownType, UpType> thisQuery = this;
        return new QuerySupplier<BottomType, UpType>() {
            @Override
            public <T> Query<UpType, T> andThen(Query<BottomType, T> finalQuery) {
                return thisQuery.andThen(subQuery.andThen(finalQuery));
            }
        };
    }

    public static <RootType extends EntryType> QuerySupplier<RootType, RootType> root(){
        return new QuerySupplier<RootType, RootType>() {
            @Override
            public <T> Query<RootType, T> andThen(Query<RootType, T> subQuery) {
                return subQuery;
            }
        };
    }

}
