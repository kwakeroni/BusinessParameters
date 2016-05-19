package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.model.ParameterGroup;
import be.kwakeroni.parameters.client.model.basic.Mapped;
import be.kwakeroni.parameters.client.model.basic.Ranged;
import be.kwakeroni.parameters.client.model.basic.Simple;

import java.time.DayOfWeek;

import static be.kwakeroni.parameters.client.model.basic.BasicEntryTypes.*;

public class TVProgram implements ParameterGroup<Mapped<Dag, Ranged<Slot, Simple>>>,
                                 Mapped<Dag, Ranged<Slot, Simple>>{

    private final Mapped<Dag, Ranged<Slot, Simple>> delegate;

    private TVProgram(BusinessParameters parameters) {
        this.delegate = mapped( Dag::name,
                            ranged(Slot::toString,
                                in( parameters.forGroup(this) )) ).getEntryType();
    }

    public static final Parameter<String> NAME = () -> "name";

    @Override
    public String getName() {
        return "scratch.triangle";
    }

    @Override
    public Ranged<Slot, Simple> forKey(Dag key) {
        return delegate.forKey(key);
    }

    public static TVProgram using(BusinessParameters parameters){
        return new TVProgram(parameters);
    }
}
