package be.kwakeroni.evelyn.client;

import be.kwakeroni.evelyn.model.Event;

import java.time.LocalDateTime;

final class DefaultEvent implements Event {

    private final String user;
    private final String operation;
    private final String objectId;
    private final LocalDateTime time;
    private final String data;

    public DefaultEvent(String user, String operation, String objectId, LocalDateTime time, String data) {
        this.user = user;
        this.operation = operation;
        this.objectId = objectId;
        this.time = time;
        this.data = data;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public String getData() {
        return data;
    }
}
