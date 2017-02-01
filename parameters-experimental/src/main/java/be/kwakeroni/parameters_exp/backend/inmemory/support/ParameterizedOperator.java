package be.kwakeroni.parameters_exp.backend.inmemory.support;

import java.util.function.UnaryOperator;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public interface ParameterizedOperator<T, Param> {

    T call(T t, Param parameter);

    default UnaryOperator<T> with(Param parameter){
        return t -> call(t, parameter);
    }

    static <T, Param> ParameterizedOperator<T, Param> apply(ParameterizedOperator<T, Param> op){
        return op;
    }

}
