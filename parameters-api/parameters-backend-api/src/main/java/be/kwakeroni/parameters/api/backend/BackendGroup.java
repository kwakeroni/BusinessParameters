package be.kwakeroni.parameters.api.backend;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface BackendGroup<Q> {

    default <ES extends BackendGroup<Q>> ES as(Class<? super ES> type){
        return (ES) type.cast(this);
    }

}
