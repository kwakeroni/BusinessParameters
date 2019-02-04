package be.kwakeroni.parameters.types.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static be.kwakeroni.parameters.types.support.ParameterTypes.LOCAL_DATE;
import static org.assertj.core.api.Assertions.assertThat;

class JavaLangTypeTest {

    @Nested
    @DisplayName("LOCAL_DATE")
    class LocalDateTest {

        @Test
        @DisplayName("Is reversible")
        void isReversible() {
            LocalDate acDate = LocalDate.of(44, Month.MARCH, 15);
            String acDateString = LOCAL_DATE.toString(acDate);
            assertThat(LOCAL_DATE.fromString(acDateString)).isEqualTo(acDate);
        }

        @Test
        @DisplayName("Supports years of the BC era")
        void supportsBCYears() {
            LocalDate idesMarch = LocalDate.of(-44, Month.MARCH, 15);
            String idesMarchString = LOCAL_DATE.toString(idesMarch);
            assertThat(LOCAL_DATE.fromString(idesMarchString)).isEqualTo(idesMarch);
        }

    }

}