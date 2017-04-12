package be.kwakeroni.parameters.types.support;

import java.util.function.Function;

/**
 * Created by kwakeroni on 12.04.17.
 */
class AdhocBasicType<T, B> extends AdhocType<T> implements BasicType<T, B> {

    private final JavaLangType javaLangType;
    private final Function<? super B, ? extends T> fromBasic;
    private final Function<? super T, ? extends B> toBasic;

    public AdhocBasicType(Function<? super String, ? extends T> fromString, Function<? super T, String> toString, JavaLangType javaLangType, Function<? super B, ? extends T> fromBasic, Function<? super T, ? extends B> toBasic) {
        super(fromString, toString);
        this.javaLangType = javaLangType;
        this.fromBasic = fromBasic;
        this.toBasic = toBasic;
    }

    public AdhocBasicType(Class<T> type, Function<? super String, ? extends T> fromString, Function<? super T, String> toString, JavaLangType javaLangType, Function<? super B, ? extends T> fromBasic, Function<? super T, ? extends B> toBasic) {
        super(type, fromString, toString);
        this.javaLangType = javaLangType;
        this.fromBasic = fromBasic;
        this.toBasic = toBasic;
    }

    @Override
    public JavaLangType asBasicType() {
        return javaLangType;
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
