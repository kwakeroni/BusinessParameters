package be.kwakeroni.scratch.tv;

import be.kwakeroni.parameters.types.api.ParameterType;
import be.kwakeroni.parameters.types.support.ParameterTypes;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public enum Dag {
    MAANDAG, DINSDAG, WOENSDAG, DONDERDAG, VRIJDAG, ZATERDAG, ZONDAG;

    public static final ParameterType<Dag> type = ParameterTypes.ofEnum(Dag.class);
}
