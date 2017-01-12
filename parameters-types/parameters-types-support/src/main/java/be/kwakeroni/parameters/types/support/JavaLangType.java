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
    },
    INT {
        @Override
        public Object fromString(String value) {
            return Integer.parseInt(value);
        }

        @Override
        public String toString(Object value) {
            return Integer.toString((int) value);
        }
    },
    LONG {
        @Override
        public Object fromString(String value) {
            return Long.parseLong(value);
        }

        @Override
        public String toString(Object value) {
            return Long.toString((long) value);
        }
    },
    BOOLEAN {
        @Override
        public Object fromString(String value) {
            return Boolean.parseBoolean(value);
        }

        @Override
        public String toString(Object value) {
            return Boolean.toString((boolean) value);
        }
    },
    CHAR {
        @Override
        public Object fromString(String value) {
            return value.charAt(0);
        }

        @Override
        public String toString(Object value) {
            return Character.toString((char) value);
        }
    };
}
