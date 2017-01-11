package be.kwakeroni.parameters.basic.client.support;

import be.kwakeroni.parameters.basic.client.model.Range;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class Ranges {

//    public static <T> ParameterizedParameterType<Range<T>> ofType(ComparableParameterType<T> type) {
//        return ParameterizedParameterType.of(
//                toString(type::convertToString),
//                fromString(type::convertFromString, type::compare),
//                type
//        );
//    }

    // @todo: Escaping
    public static String toRangeString(String from, String to){
        return "[" + from + "," + to + "]";
    }

    public static <T> String toRangeString(T from, T to, Function<T, String> typeConverter){
        return toRangeString(typeConverter.apply(from), typeConverter.apply(to));
    }

    public static <T> Function<Range<T>, String> toStringOf(Function<T, String> typeConverter) {
        return range -> toRangeString(range.getFrom(), range.getTo(), typeConverter);
    }


    public static <T> Range<T> fromString(String rangeString, Function<String, ? extends T> typeConverter, Comparator<? super T> comparator){
        String[] fromTo = toStringPair(rangeString);
        T from = typeConverter.apply(fromTo[0]);
        T to = typeConverter.apply(fromTo[1]);
        return Range.of(from, to, comparator);
    }

    public static <T extends Comparable<? super T>> Range<T> fromString(String rangeString, Function<String, ? extends T> typeConverter){
        String[] fromTo = toStringPair(rangeString);
        T from = typeConverter.apply(fromTo[0]);
        T to = typeConverter.apply(fromTo[1]);
        return Range.of(from, to);
    }

    public static <T> Function<String, Range<T>> fromStringOf(Function<String, ? extends T> typeConverter, Comparator<? super T> comparator) {
        return string -> fromString(string, typeConverter, comparator);
    }

    public static <T extends Comparable<? super T>> Function<String, Range<T>> fromStringOf(Function<String, T> typeConverter) {
        return string -> fromString(string, typeConverter);
    }

    public static <T> boolean containsValue(String rangeString, String value, Function<String, ? extends T> typeConverter, Comparator<? super T> comparator){
        return Ranges.<T> fromString(rangeString, typeConverter, comparator).contains(typeConverter.apply(value));
    }

    public static <T extends Comparable<? super T>> boolean containsValue(String rangeString, String value, Function<String, ? extends T> typeConverter){
        return Ranges.<T> fromString(rangeString, typeConverter).contains(typeConverter.apply(value));
    }

    public static <T> BiPredicate<String, String> containsValueOf(Function<String, ? extends T> typeConverter, Comparator<? super T> comparator){
        return (rangeString, valueString) -> containsValue(rangeString, valueString, typeConverter, comparator);
    }

    public static <T extends Comparable<? super T>> BiPredicate<String, String> containsValueOf(Function<String, ? extends T> typeConverter){
        return (rangeString, valueString) -> containsValue(rangeString, valueString, typeConverter);
    }

//
//    private static Function<String, Range<String>> fromString(Comparator<? super String> comparator) {
//        return string -> {
//            String[] fromTo = toStringPair(string);
//            return new DefaultRange<>(fromTo[0], fromTo[1], comparator);
//        };
//    }

    // @todo: Escaping
    private static String[] toStringPair(String rangeRepresentation) {
        Matcher matcher = PATTERN.matcher(rangeRepresentation);
        if (matcher.matches()) {
            return new String[]{matcher.group(1), matcher.group(2)};
        } else {
            throw new IllegalArgumentException("Incorrect Range representation: " + rangeRepresentation);
        }
    }

    private static final Pattern PATTERN = Pattern.compile("^\\[(.+)\\,(.+)\\]$");


}
