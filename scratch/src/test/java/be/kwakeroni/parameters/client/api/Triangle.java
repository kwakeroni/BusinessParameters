package be.kwakeroni.parameters.client.api;

import be.kwakeroni.parameters.client.model.ParameterGroup;
import be.kwakeroni.parameters.client.model.basic.BasicEntryType;
import be.kwakeroni.parameters.client.model.basic.Ranged;
import be.kwakeroni.parameters.client.model.basic.Simple;

import static be.kwakeroni.parameters.client.model.basic.BasicEntryType.*;

public class Triangle implements ParameterGroup<Ranged<Integer, Ranged<Character, Simple>>> {

    private static final Triangle INSTANCE = new Triangle();

    private static Triangle group(){
        return INSTANCE;
    }

    private Triangle() {
    }

    @Override
    public String getName() {
        return "scratch.triangle";
    }

    public Ranged<Integer, Ranged<Character, Simple>> using(BusinessParameters parameters){
        return ranged( Integer.class,
                ranged(Character.class,
                        in( parameters.forGroup(this) )) )
                .toEntryType();
    }

    public Ranged<Character, Simple> rusing(BusinessParameters parameters){
        return ranged(Character.class,
                    in( parameters.forGroup(this) )).toEntryType();
    }

    public Simple susing(BusinessParameters parameters){
        return in (parameters.forGroup((ParameterGroup<Simple>)null)).toEntryType();
    }
}
