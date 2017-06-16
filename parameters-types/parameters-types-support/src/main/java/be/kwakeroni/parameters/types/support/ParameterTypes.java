package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ParameterTypes {

    public static BasicType<String, String> STRING = JavaLangType.STRING;
    public static BasicType<Integer, Integer> INT = JavaLangType.INT;
    public static BasicType<Long, Long> LONG = JavaLangType.LONG;
    public static BasicType<Boolean, Boolean> BOOLEAN = JavaLangType.BOOLEAN;
    public static BasicType<Character, Character> CHAR = JavaLangType.CHAR;

    public static <E extends Enum<E>> ParameterType<E> ofEnum(Class<E> type) {
        return new EnumType<>(type);
    }

    public static <T> ParameterType<T> of(Class<T> type, Function<? super String, ? extends T> fromString, Function<? super T, String> toString) {
        return new AdhocType<>(type, fromString, toString);
    }

    public static <T> ParameterType<T> of(Function<? super String, ? extends T> fromString, Function<? super T, String> toString) {
        return new AdhocType<>(fromString, toString);
    }

    public static <T> BasicType<T, Integer> ofIntegerType(Class<T> type, Function<? super String, ? extends T> fromString, Function<? super T, String> toString,
                                                          Function<? super Integer, ? extends T> fromBasic, Function<? super T, Integer> toBasic) {
        return new AdhocBasicType<>(type, fromString, toString, JavaLangType.INT, fromBasic, toBasic);
    }

    public static <T> BasicType<T, Integer> ofIntegerType(Function<? super String, ? extends T> fromString, Function<? super T, String> toString,
                                                          Function<? super Integer, ? extends T> fromBasic, Function<? super T, Integer> toBasic) {
        return new AdhocBasicType<>(fromString, toString, JavaLangType.INT, fromBasic, toBasic);
    }

}
