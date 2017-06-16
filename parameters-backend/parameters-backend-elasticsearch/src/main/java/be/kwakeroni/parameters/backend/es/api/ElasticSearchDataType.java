package be.kwakeroni.parameters.backend.es.api;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ElasticSearchDataType<T> {

    public static ElasticSearchDataType<Integer> INTEGER = Standard.INTEGER;

    public Object toJSONRepresentation(T value);

    public static enum Standard implements ElasticSearchDataType {
        INTEGER {
            @Override
            public Object toJSONRepresentation(Object value) {
                return (Integer) value;
            }
        };
    }

}
