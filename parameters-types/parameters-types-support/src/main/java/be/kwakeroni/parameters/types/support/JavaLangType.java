package be.kwakeroni.parameters.types.support;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public enum JavaLangType implements BasicType {
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


    @Override
    public JavaLangType asBasicType() {
        return this;
    }

    @Override
    public Object toBasic(Object value) {
        return value;
    }

    @Override
    public Object fromBasic(Object value) {
        return value;
    }


    @Override
    public int compare(Object o1, Object o2) {
        return compareGeneric(o1, o2);
    }

    private <T extends Comparable<T>> int compareGeneric(Object o1, Object o2) {
        return ((T) o1).compareTo((T) o2);
    }
}
