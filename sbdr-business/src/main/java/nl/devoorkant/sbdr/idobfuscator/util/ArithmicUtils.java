package nl.devoorkant.sbdr.idobfuscator.util;

public class ArithmicUtils {
    /**
     * Returns the unsigned remainder from dividing the first argument
     * by the second where each argument and the result is interpreted
     * as an unsigned value.
     * <p>This method does not use the {@code BigInteger} datatype.</p>
     *
     * @param dividend the value to be divided
     * @param divisor the value doing the dividing
     * @return the unsigned remainder of the first argument divided by
     * the second argument.
     *
     * @since 4.0
     */
    public static long remainderUnsigned(long dividend, long divisor) {
        if (divisor >= 0L) {
            if (dividend >= 0L) {
                return dividend % divisor;
            }
            // The implementation is a Java port of algorithm described in the book
            // "Hacker's Delight" (section "Unsigned short division from signed division").
            long q = ((dividend >>> 1) / divisor) << 1;
            dividend -= q * divisor;
            if (dividend < 0L || dividend >= divisor) {
                dividend -= divisor;
            }
            return dividend;
        }
        return dividend >= 0L || dividend < divisor ? dividend : dividend - divisor;
    }
    
    /**
     * Returns the unsigned quotient of dividing the first argument by
     * the second where each argument and the result is interpreted as
     * an unsigned value.
     * <p>Note that in two's complement arithmetic, the three other
     * basic arithmetic operations of add, subtract, and multiply are
     * bit-wise identical if the two operands are regarded as both
     * being signed or both being unsigned. Therefore separate {@code
     * addUnsigned}, etc. methods are not provided.</p>
     * <p>This method does not use the {@code BigInteger} datatype.</p>
     *
     * @param dividend the value to be divided
     * @param divisor the value doing the dividing
     * @return the unsigned quotient of the first argument divided by
     * the second argument.
     *
     * @since 4.0
     */
    public static long divideUnsigned(long dividend, long divisor) {
        if (divisor >= 0L) {
            if (dividend >= 0L) {
                return dividend / divisor;
            }
            // The implementation is a Java port of algorithm described in the book
            // "Hacker's Delight" (section "Unsigned short division from signed division").
            long q = ((dividend >>> 1) / divisor) << 1;
            dividend -= q * divisor;
            if (dividend < 0L || dividend >= divisor) {
               q++;
            }
            return q;
        }
        return dividend >= 0L || dividend < divisor ? 0L : 1L;
    }
    
}
