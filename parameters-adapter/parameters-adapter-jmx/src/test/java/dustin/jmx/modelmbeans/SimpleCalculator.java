package dustin.jmx.modelmbeans;

/**
 * Simple calculator class intended to demonstrate how a class with no knowledge
 * of JMX or management can be "wrapped" with ModelMBeans.
 *
 * @author Dustin
 */
public class SimpleCalculator
{
    /**
     * Calculate the sum of the augend and the addend.
     *
     * @param augend First integer to be added.
     * @param addend Second integer to be added.
     * @return Sum of augend and addend.
     */
    public int add(final int augend, final int addend)
    {
        return augend + addend;
    }

    /**
     * Calculate the difference between the minuend and subtrahend.
     *
     * @param minuend Minuend in subtraction operation.
     * @param subtrahend Subtrahend in subtraction operation.
     * @return Difference of minuend and subtrahend.
     */
    public int subtract(final int minuend, final int subtrahend)
    {
        return minuend - subtrahend;
    }

    /**
     * Calculate the product of the two provided factors.
     *
     * @param factor1 First integer factor.
     * @param factor2 Second integer factor.
     * @return Product of provided factors.
     */
    public int multiply(final int factor1, final int factor2)
    {
        return factor1 * factor2;
    }

    /**
     * Calculate the quotient of the dividend divided by the divisor.
     *
     * @param dividend Integer dividend.
     * @param divisor Integer divisor.
     * @return Quotient of dividend divided by divisor.
     */
    public double divide(final int dividend, final int divisor)
    {
        return dividend / divisor;
    }
}