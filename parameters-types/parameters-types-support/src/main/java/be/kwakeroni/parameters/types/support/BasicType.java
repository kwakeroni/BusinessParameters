package be.kwakeroni.parameters.types.support;

import be.kwakeroni.parameters.types.api.ParameterType;

import java.util.Comparator;

/**
 * Created by kwakeroni on 12.04.17.
 */
public interface BasicType<T, B> extends ParameterType<T>, Comparator<B> {

    public JavaLangType asBasicType();

    public B toBasic(T value);

    public T fromBasic(B value);

    @Override
    default int compare(B o1, B o2) {
        return asBasicType().compare(o1, o2);
    }
}
