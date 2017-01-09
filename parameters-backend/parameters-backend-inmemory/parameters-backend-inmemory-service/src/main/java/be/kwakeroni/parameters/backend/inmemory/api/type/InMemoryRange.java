package be.kwakeroni.parameters.backend.inmemory.api.type;

import java.util.Comparator;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class InMemoryRange<V> implements Range<V> {

    private final Comparator<? super V> comparator;
    private final V from;
    private final V to;

    private InMemoryRange(V from, V toExclusive, Comparator<? super V> comparator) {
        this.comparator = comparator;
        this.from = from;
        this.to = toExclusive;
    }

    @Override
    public V getFrom() {
        return from;
    }

    @Override
    public V getTo() {
        return to;
    }

    public boolean contains(V value) {
        return comparator.compare(value, this.to) < 0 && comparator.compare(value, this.from) >= 0;
    }

    public static <T> ParameterizedParameterType<Range<T>> ofType(ComparableParameterType<T> type) {
        return ParameterizedParameterType.of(
                toString(type::convertToString),
                fromString(type::convertFromString, type::compare),
                type
        );
    }

    private static <T> Function<Range<T>, String> toString(Function<T, String> typeConverter) {
        return range -> {
            return "[" + typeConverter.apply(range.getFrom()) + "," + typeConverter.apply(range.getTo()) + "]";
        };
    }

    private static <T> Function<String, Range<T>> fromString(Function<String, T> typeConverter, Comparator<? super T> comparator) {
        return string -> {
            String[] fromTo = toStringPair(string);
            T from = typeConverter.apply(fromTo[0]);
            T to = typeConverter.apply(fromTo[1]);
            return new InMemoryRange<>(from, to, comparator);
        };
    }

    private static Function<String, Range<String>> fromString(Comparator<? super String> comparator) {
        return string -> {
            String[] fromTo = toStringPair(string);
            return new InMemoryRange<>(fromTo[0], fromTo[1], comparator);
        };
    }

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
