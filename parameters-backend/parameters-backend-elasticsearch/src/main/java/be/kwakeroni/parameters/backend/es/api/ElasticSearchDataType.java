package be.kwakeroni.parameters.backend.es.api;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ElasticSearchDataType<T> {

    public static ElasticSearchDataType<Integer> INTEGER = Standard.INTEGER;

    public Standard asStandard();

    public static enum Standard implements ElasticSearchDataType {
        INTEGER;


        @Override
        public Standard asStandard() {
            return this;
        }
    }

}
