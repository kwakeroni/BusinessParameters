package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static be.kwakeroni.parameters.types.support.ParameterTypes.STRING;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
@RunWith(Parameterized.class)
public class ParameterTypesTest<T> {

    private static final List<Object[]> MAPPINGS = new ArrayList<>();
    private static final ParameterType<TestEnum> TEST_ENUM = ParameterTypes.ofEnum(TestEnum.class);
    private static final ParameterType<byte[]> BYTE_ARRAY = ParameterTypes.of(byte[].class, String::getBytes, String::new);
    private static final ParameterType<byte[]> ANONYMOUS = ParameterTypes.of(String::getBytes, String::new);

    static {
        expectMapping(STRING, "myString", "myString");
        expectMapping(TEST_ENUM, TestEnum.ONE, "ONE");
        expectMapping(TEST_ENUM, TestEnum.TWO, "TWO");
        expectMapping(TEST_ENUM, TestEnum.THREE, "THREE");
        expectMapping(BYTE_ARRAY, new byte[]{65, 66, 67}, "ABC");
        expectMapping(ANONYMOUS, new byte[]{65, 66, 67}, "ABC");
    }

    @Parameter(0)
    public ParameterType<T> type;
    @Parameter(1)
    public T value;
    @Parameter(2)
    public String stringValue;

    @Test
    public void testToString(){
        assertThat(type.toString(value)).isEqualTo(stringValue);
    }

    @Test
    public void testFromString(){
        assertThat(type.fromString(stringValue)).isEqualTo(value);
    }

    @Parameters(name = "{0} : {2}")
    public static Iterable<Object[]> data() {
        return MAPPINGS;
    }

    private static <T> void expectMapping(ParameterType<? super T> type, T value, String stringValue) {
        MAPPINGS.add(mapping(type, value, stringValue));
    }

    private static <T> Object[] mapping(ParameterType<? super T> type, T value, String stringValue) {
        return new Object[]{type, value, stringValue};
    }

    static enum TestEnum {
        ONE,
        TWO,
        THREE;
    }

}
