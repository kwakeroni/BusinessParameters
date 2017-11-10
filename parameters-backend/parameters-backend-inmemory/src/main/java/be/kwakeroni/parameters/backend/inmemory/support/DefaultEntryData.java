package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;

import java.util.*;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DefaultEntryData implements EntryData {

    private final String id = UUID.randomUUID().toString();

    private final Map<String, String> map;

    private DefaultEntryData(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public String getValue(String parameterName) {
        return map.get(parameterName);
    }

    @Override
    public void setValue(String parameterName, String value) {
        if (this.map.containsKey(parameterName)) {
            this.map.put(parameterName, value);
        } else {
            throw new IllegalArgumentException("Unknown parameter: " + parameterName);
        }
    }

    @Override
    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(this.map);
    }

    public static DefaultEntryData of(String parameter, String value, String... andSoOn) {
        if (andSoOn.length % 2 != 0) {
            throw new IllegalArgumentException("Expected parameter-value pairs: " + Arrays.toString(andSoOn));
        }

        Map<String, String> map = new HashMap<>(andSoOn.length / 2 + 1);
        map.put(parameter, value);
        for (int i = 0; i < andSoOn.length; i += 2) {
            map.put(andSoOn[i], andSoOn[i + 1]);
        }

        return new DefaultEntryData(map);
    }

    public static DefaultEntryData of(Map<String, String> entry){
        return new DefaultEntryData(new HashMap<>(entry));
    }

//    public static DefaultEntryData of(Parameter<?> parameter, String value, Object... andSoOn){
//        if (andSoOn.length % 2 != 0){
//            throw new IllegalArgumentException("Expected parameter-value pairs: " + Arrays.toString(andSoOn));
//        }
//
//        Map<String, String> map = new HashMap<>(andSoOn.length/2 + 1);
//        map.put(parameter.getName(), value);
//        for (int i=0; i<andSoOn.length; i+=2){
//            map.put( ((Parameter<?>) andSoOn[i]).getName(), (String) andSoOn[i+1]);
//        }
//
//        return new DefaultEntryData(map);
//    }

}
