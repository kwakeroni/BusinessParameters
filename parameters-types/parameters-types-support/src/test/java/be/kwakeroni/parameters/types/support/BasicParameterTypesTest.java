package be.kwakeroni.parameters.types.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static be.kwakeroni.parameters.types.support.ParameterTypes.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
@RunWith(Parameterized.class)
public class BasicParameterTypesTest<T, B> {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static final Calendar SOME_PAST_CALENDAR = calendar(1981, Calendar.DECEMBER, 14, 15, 16, 17, 891);

    private static final List<Object[]> MAPPINGS = new ArrayList<>();
    private static final BasicType<Calendar, Long> CALENDAR = ParameterTypes.ofLongType(Calendar.class, BasicParameterTypesTest::parseCalendar, BasicParameterTypesTest::toString, BasicParameterTypesTest::newCalendar, Calendar::getTimeInMillis);
    private static final BasicType<Calendar, Long> ANONYMOUS = ParameterTypes.ofLongType(BasicParameterTypesTest::parseCalendar, BasicParameterTypesTest::toString, BasicParameterTypesTest::newCalendar, Calendar::getTimeInMillis);


    static {
        expectStandardMapping(STRING, "myString", "myString", "yourString");
        expectStandardMapping(INT, 18, "18", 19);
        expectStandardMapping(LONG, 18L, "18", 19L);
        expectStandardMapping(BOOLEAN, true, "true", null);
        expectStandardMapping(BOOLEAN, false, "false", true);
        expectStandardMapping(CHAR, '\u00C7', "Ç", '\u00C8');
        expectStandardMapping(LOCAL_DATE, LocalDate.of(1981, 12, 14), "19811214", LocalDate.of(2019, 1, 2));
        expectMapping(CALENDAR, Long.class, SOME_PAST_CALENDAR, SOME_PAST_CALENDAR.getTimeInMillis(), "19811214151617891", System.currentTimeMillis());
        expectMapping(ANONYMOUS, Long.class, SOME_PAST_CALENDAR, SOME_PAST_CALENDAR.getTimeInMillis(), "19811214151617891", System.currentTimeMillis());
    }

    @Parameter(0)
    public BasicType<T, B> type;
    @Parameter(1)
    public Class<B> javaType;
    @Parameter(2)
    public T value;
    @Parameter(3)
    public B basicValue;
    @Parameter(4)
    public String stringValue;
    @Parameter(5)
    public B largerBasicValue;

    @Test
    public void asBasicType() {
        assertThat(type.getBasicJavaClass()).isSameAs(javaType);
    }

    @Test
    public void testToString() {
        assertThat(type.toString(value)).isEqualTo(stringValue);
    }

    @Test
    public void testFromString() {
        assertThat(type.fromString(stringValue)).isEqualTo(value);
    }

    @Test
    public void testToBasic() {
        assertThat(type.toBasic(value)).isEqualTo(basicValue);
    }

    @Test
    public void fromBasic() {
        assertThat(type.fromBasic(basicValue)).isEqualTo(value);
    }

    @Test
    public void compare() {
        if (value != Boolean.TRUE) {
            assertThat(type.compare(basicValue, largerBasicValue)).isLessThan(0);
            assertThat(type.compare(largerBasicValue, basicValue)).isGreaterThan(0);
        }
        assertThat(type.compare(basicValue, basicValue)).isEqualTo(0);
    }

    @Parameters(name = "{0} : {2}")
    public static Iterable<Object[]> data() {
        return MAPPINGS;
    }

    private static <T> void expectStandardMapping(BasicType<T, T> javaType, T value, String stringValue, T largerBasicValue) {
        expectMapping(javaType, javaType.getBasicJavaClass(), value, value, stringValue, largerBasicValue);
    }

    private static <T, B> void expectMapping(BasicType<? super T, ? super B> type, Class<B> javaType, T value, B basicValue, String stringValue, B largerBasicValue) {
        MAPPINGS.add(mapping(type, javaType, value, basicValue, stringValue, largerBasicValue));
    }

    private static <T, B> Object[] mapping(BasicType<? super T, ? super B> type, Class<B> javaType, T value, B basicValue, String stringValue, B largerBasicValue) {
        return new Object[]{type, javaType, value, basicValue, stringValue, largerBasicValue};
    }

    private static Calendar calendar(int year, int month, int day, int hours, int minutes, int seconds, int millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hours, minutes, seconds);
        calendar.set(Calendar.MILLISECOND, millis);
        return calendar;
    }

    private static Calendar parseCalendar(String dateString) {
        try {
            Date date = FORMAT.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toString(Calendar calendar) {
        return FORMAT.format(calendar.getTime());
    }

    private static Calendar newCalendar(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }
}
