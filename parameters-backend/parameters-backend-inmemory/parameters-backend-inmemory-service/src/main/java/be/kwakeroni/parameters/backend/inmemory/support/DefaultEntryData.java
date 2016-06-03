package be.kwakeroni.parameters.backend.inmemory.support;

import be.kwakeroni.parameters.backend.inmemory.api.EntryData;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class DefaultEntryData implements EntryData {

    private Map<String, String> map;

    private DefaultEntryData(Map<String, String> map) {
        this.map = Collections.unmodifiableMap(map);
    }

    @Override
    public String getValue(String parameterName) {
        return map.get(parameterName);
    }

    @Override
    public Map<String, String> asMap() {
        return this.map;
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
