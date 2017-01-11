package be.kwakeroni.parameters.backend.api;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BackendGroup<Q> {

    public default <ES extends BackendGroup<Q>> ES as(Class<? super ES> type){
        return (ES) type.cast(this);
    }

}
