package be.kwakeroni.parameters.test;

import be.kwakeroni.parameters.backend.inmemory.api.type.ComparableParameterType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class Slot implements Comparable<Slot> {

    private final int hour;
    private final boolean halfPast;

    private Slot(int hour, boolean halfPast) {
        this.hour = hour;
        this.halfPast = halfPast;
    }

    @Override
    public int compareTo(Slot o) {
        return (this.hour == o.hour) ?
                Boolean.compare(this.halfPast, o.halfPast) :
                Integer.compare(this.hour, o.hour);
    }

    public String toString() {
        return hour + ((halfPast) ? ".5" : ".0");
    }

    private static final Pattern STRING_PATTERN = Pattern.compile("([1-2]?[0-9])\\.(0|5)");

    public static Slot fromString(String string) {
        Matcher matcher = STRING_PATTERN.matcher(string);
        if (matcher.matches()) {
            int hour = Integer.parseInt(matcher.group(1));
            int halfPast = Integer.parseInt(matcher.group(2));
            if (hour < 24) {
                return new Slot(hour, (halfPast == 5));
            }
        }
        throw new IllegalArgumentException("Incorrect Slot format: " + string);
    }

    public static ComparableParameterType<Slot> TYPE = ComparableParameterType.of(Slot::toString, Slot::fromString);

    public static Slot atHour(int hour) {
        return new Slot(hour, false);
    }

    public static Slot atHalfPast(int hour) {
        return new Slot(hour, true);
    }

}
