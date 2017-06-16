package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.types.support.BasicType;
import be.kwakeroni.parameters.types.support.ParameterTypes;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class Slot implements Comparable<Slot> {

    public static final BasicType<Slot, Integer> type = ParameterTypes.ofIntegerType(Slot.class, Slot::fromString, Slot::toString, Slot::fromInt, Slot::toInt);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return hour == slot.hour &&
                halfPast == slot.halfPast;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, halfPast);
    }

    public int toInt() {
        return 10 * hour + ((halfPast) ? 5 : 0);
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

    public static Slot fromInt(Integer integer) {
        if (integer % 10 == 0) {
            return atHour(integer / 10);
        } else if (integer % 10 == 5) {
            return atHalfPast(integer / 10);
        } else {
            throw new IllegalArgumentException("Integer value " + integer + " is no valid representation of a Slot");
        }
    }

    public static Slot atHour(int hour) {
        return new Slot(hour, false);
    }

    public static Slot atHalfPast(int hour) {
        return new Slot(hour, true);
    }

}
