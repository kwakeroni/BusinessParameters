package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Function;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public interface ParameterTypes {

    public static final BasicType<String, String> STRING = JavaLangType.STRING;
    public static final BasicType<Integer, Integer> INT = JavaLangType.INT;
    public static final BasicType<Long, Long> LONG = JavaLangType.LONG;
    public static final BasicType<Boolean, Boolean> BOOLEAN = JavaLangType.BOOLEAN;
    public static final BasicType<Character, Character> CHAR = JavaLangType.CHAR;
    public static final BasicType<LocalDate, LocalDate> LOCAL_DATE = JavaLangType.LOCAL_DATE;

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

    public static <T> BasicType<T, Long> ofLongType(Class<T> type, Function<? super String, ? extends T> fromString, Function<? super T, String> toString,
                                                          Function<? super Long, ? extends T> fromBasic, Function<? super T, Long> toBasic) {
        return new AdhocBasicType<>(type, fromString, toString, JavaLangType.LONG, fromBasic, toBasic);
    }

    public static <T> BasicType<T, Long> ofLongType(Function<? super String, ? extends T> fromString, Function<? super T, String> toString,
                                                          Function<? super Long, ? extends T> fromBasic, Function<? super T, Long> toBasic) {
        return new AdhocBasicType<>(fromString, toString, JavaLangType.LONG, fromBasic, toBasic);
    }

}
