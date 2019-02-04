package be.kwakeroni.parameters.backend.es.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ElasticSearchDataType<T> {

    public static ElasticSearchDataType<Integer> INTEGER = integer -> (Integer) integer;
    public static ElasticSearchDataType<Long> LONG = longVal -> (Long) longVal;
    public static ElasticSearchDataType<Float> FLOAT = floatVal -> (Float) floatVal;
    public static ElasticSearchDataType<Double> DOUBLE = doubleVal -> (Double) doubleVal;
    public static ElasticSearchDataType<Boolean> BOOLEAN = bool -> (Boolean) bool;
    public static ElasticSearchDataType<String> STRING = string -> (String) string;
    public static ElasticSearchDataType<LocalDate> LOCAL_DATE = date -> date.format(Format.LOCAL_DATE_FORMATTER);


    public Object toJSONRepresentation(T value);

    /*
     * Supported type mappings:
     *   JSONObject             JSON            Detected ES Types
     *   ---------------------  --------------  -----------------
     * - JSONObject             object          object
     * - JSONArray              array           array (recursive)
     * - Map                    object          object
     * - Collection             array           array (recursive)
     * - array                  array           array (recursive)
     * - Number (whole)         integer         long
     * - Number (fp)            fp              float
     * - Boolean                boolean         boolean
     * - JSONString             string          date|double|long|text (format detection)
     * - other: use toString    string          date|double|long|text (format detection)
     *
     * See: https://www.elastic.co/guide/en/elasticsearch/reference/current/dynamic-field-mapping.html
     */

    public static class Format {
        private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    }
}

