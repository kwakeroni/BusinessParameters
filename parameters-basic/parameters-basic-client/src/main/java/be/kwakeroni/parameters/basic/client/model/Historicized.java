package be.kwakeroni.parameters.basic.client.model;

import be.kwakeroni.parameters.client.api.model.EntryType;

import java.time.LocalDate;

public interface Historicized<ET extends EntryType> extends Ranged<LocalDate, ET> {
}
