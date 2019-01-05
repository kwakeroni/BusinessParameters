package be.kwakeroni.parameters.basic.client.model;

import be.kwakeroni.parameters.client.api.model.EntryType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.extension.mockito.MockitoExtension;
import org.mockito.Mock;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoricizedTest {

    @Mock
    Consumer<LocalDate> atValue;
    @Mock
    EntryType entryType;

    private Historicized<EntryType> group = new Historicized<EntryType>() {
        @Override
        public EntryType at(LocalDate value) {
            atValue.accept(value);
            return entryType;
        }
    };

    @Test
    void testNow() {
        LocalDate today = LocalDate.now();
        EntryType entry = group.now();
        assertThat(entry).isSameAs(entryType);
        verify(atValue).accept(today);
    }

    @Test
    void testAtDate() throws Exception {
        Date date = new SimpleDateFormat("dd-MM-yyyy").parse("14-12-1981");
        EntryType entry = group.at(date);
        assertThat(entry).isSameAs(entryType);
        verify(atValue).accept(LocalDate.of(1981, Month.DECEMBER, 14));
    }

    @Test
    void testAtCalendar() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1981);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 14);
        EntryType entry = group.at(calendar);
        assertThat(entry).isSameAs(entryType);
        verify(atValue).accept(LocalDate.of(1981, Month.DECEMBER, 14));
    }

}