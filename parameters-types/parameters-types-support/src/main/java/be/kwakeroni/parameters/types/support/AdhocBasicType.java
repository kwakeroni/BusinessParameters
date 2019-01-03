package be.kwakeroni.parameters.types.support;

import java.util.function.Function;

/**
 * Created by kwakeroni on 12.04.17.
 */
class AdhocBasicType<T, B extends Comparable<B>> extends AdhocType<T> implements BasicType<T, B> {

    private final Class<B> basicClass;
    private final Function<? super B, ? extends T> fromBasic;
    private final Function<? super T, ? extends B> toBasic;

    public AdhocBasicType(Function<? super String, ? extends T> fromString, Function<? super T, String> toString, Class<B> basicClass, Function<? super B, ? extends T> fromBasic, Function<? super T, ? extends B> toBasic) {
        super(fromString, toString);
        this.basicClass = basicClass;
        this.fromBasic = fromBasic;
        this.toBasic = toBasic;
    }

    public AdhocBasicType(Class<T> type, Function<? super String, ? extends T> fromString, Function<? super T, String> toString, Class<B> basicClass, Function<? super B, ? extends T> fromBasic, Function<? super T, ? extends B> toBasic) {
        super(type, fromString, toString);
        this.basicClass = basicClass;
        this.fromBasic = fromBasic;
        this.toBasic = toBasic;
    }

    @Override
    public Class<B> getBasicJavaClass() {
        return basicClass;
    }

    @Override
    public int compare(B o1, B o2) {
        return o1.compareTo(o2);
    }

    @Override
    public B toBasic(T value) {
        return toBasic.apply(value);
    }

    @Override
    public T fromBasic(B value) {
        return fromBasic.apply(value);
    }

}
