package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
enum JavaLangType implements ParameterType {
    STRING {
        @Override
        public Object fromString(String value) {
            return value;
        }

        @Override
        public String toString(Object value) {
            return (String) value;
        }
    }
    ;
}
