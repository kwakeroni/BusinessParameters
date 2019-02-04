package be.kwakeroni.scratch.test;

import be.kwakeroni.parameters.basic.type.Range;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.types.api.ParameterType;

class RangeTestParameter<T extends Comparable<? super T>> extends TestParameter<Range<T>> {

    final ParameterType<T> rangeValueType;
    final T lowerValue;
    final T originalLowValue;
    final T originalMidValue1;
    final T originalMidValue2;
    final T originalHighValue;
    final T otherMidValue;
    final T higherValue;

    public RangeTestParameter(Parameter<Range<T>> parameter, ParameterType<T> rangeValueType,
                              T lowerValue, T originalLowValue, T originalMidValue1, T originalMidValue2, T originalHighValue,
                              T otherLowValue, T otherMidValue, T otherHighValue, T higherValue) {
        super(parameter, Range.of(originalLowValue, originalHighValue), Range.of(otherLowValue, otherHighValue));
        this.rangeValueType = rangeValueType;
        this.lowerValue = lowerValue;
        this.originalLowValue = originalLowValue;
        this.originalMidValue1 = originalMidValue1;
        this.originalMidValue2 = originalMidValue2;
        this.originalHighValue = originalHighValue;
        this.otherMidValue = otherMidValue;
        this.higherValue = higherValue;
    }
}
