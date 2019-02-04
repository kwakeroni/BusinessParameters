package be.kwakeroni.parameters.basic.client.model;

import be.kwakeroni.parameters.client.api.model.EntryType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public interface Historicized<ET extends EntryType> extends Ranged<LocalDate, ET> {

    default ET now() {
        return at(LocalDate.now());
    }

    default ET at(Date value) {
        Instant instant = value.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        return at(zdt.toLocalDate());
    }

    default ET at(Calendar value) {
        Instant instant = value.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        return at(zdt.toLocalDate());
    }

}
