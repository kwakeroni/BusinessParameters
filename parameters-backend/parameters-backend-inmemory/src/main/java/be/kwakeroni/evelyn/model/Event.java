package be.kwakeroni.evelyn.model;

import java.time.LocalDateTime;

public interface Event {

    public String getUser();

    public LocalDateTime getTime();

    public String getOperation();

    public String getObjectId();

    public String getData();

}
