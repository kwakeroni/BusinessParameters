package be.kwakeroni.parameters.basic.type;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * (C) 2017 Maarten Van Puymbroeck
 */
public class DefaultRangeTest {


    @Test
    public void testOverlap(){
        testOverlap(false, 4, 7, 2,3);
        testOverlap(false, 4, 7, 3,4);
        testOverlap(true , 4, 7, 4,5);
        testOverlap(true , 4, 7, 5,6);
        testOverlap(true , 4, 7, 6,7);
        testOverlap(false, 4, 7, 7,8);
        testOverlap(true, 4, 7, 3,8);
        testOverlap(true, 4, 7, 4,7);
        testOverlap(true, 80, 120, 115, 120);
    }

    private void testOverlap(boolean expectOverlap, int a, int b, int x, int y){
        Range<Integer> rangeAB = Range.of(a, b);
        Range<Integer> rangeXY = Range.of(x, y);
        Assertions.assertThat(rangeAB.overlaps(rangeXY))
                .describedAs("Expected%s overlap between [%s,%s] and [%s,%s]",
                        (expectOverlap)? "" : " no", a, b, x, y)
                .isEqualTo(expectOverlap);
    }

}
