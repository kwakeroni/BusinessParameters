package be.kwakeroni.parameters.test;

import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.model.Parameter;
import be.kwakeroni.parameters.client.model.ParameterGroup;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;

import static be.kwakeroni.parameters.basic.client.model.BasicEntryTypes.*;

public class TVProgram implements ParameterGroup<Mapped<Dag, Ranged<Slot, Simple>>>,
                                 Mapped<Dag, Ranged<Slot, Simple>>{

    private final Mapped<Dag, Ranged<Slot, Simple>> delegate;

    private TVProgram(BusinessParameters parameters) {
        this.delegate = mapped( Dag::name,
                            ranged(Slot::toString,
                                in( parameters.forGroup(this) )) ).getEntryType();
    }

    public static final Parameter<String> NAME = () -> "name";
    public static final Parameter<String> DAG = () -> "dag";
    public static final Parameter<String> SLOT = () -> "slot";

    @Override
    public String getName() {
        return groupName();
    }

    @Override
    public Ranged<Slot, Simple> forKey(Dag key) {
        return delegate.forKey(key);
    }

    public static TVProgram using(BusinessParameters parameters){
        return new TVProgram(parameters);
    }

    public static String groupName(){
        return "scratch.triangle";
    }
}
