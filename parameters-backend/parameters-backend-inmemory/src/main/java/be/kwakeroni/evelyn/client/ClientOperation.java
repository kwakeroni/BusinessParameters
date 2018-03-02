package be.kwakeroni.evelyn.client;

import be.kwakeroni.evelyn.model.Event;

public interface ClientOperation<Entity> {

    public Entity operate(Entity entity, Event event);

}
