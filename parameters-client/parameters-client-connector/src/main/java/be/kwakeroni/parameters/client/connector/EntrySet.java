package be.kwakeroni.parameters.client.connector;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface EntrySet<Q> {

    default <ES extends EntrySet<Q>> ES as(Class<? super ES> type){
        return (ES) type.cast(this);
    }

}
