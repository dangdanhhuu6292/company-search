package com.eatthepath.idobfuscator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.eatthepath.idobfuscator.util.BitwiseOperationUtil;

import nl.devoorkant.sbdr.idobfuscator.util.ArithmicUtils;

/**
 * <p>Converts integers to strings using an arbitrary alphabet. As with a "normal" string representation of a number,
 * each character in a string produced by this codec represents a numeric value, and the position of the character
 * determines its place value; the radix is equal to the number of characters in the codec's alphabet.</p>
 *
 * <p>The order of characters in the codec's alphabet is significant, which allows callers to "shuffle" the alphabet
 * to frustrate unwanted attempts at deobfuscation.</p>
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
public class AlphabetCodec implements IntegerCodec {

    private final char[] alphabet;
    private final transient Map<Character, Integer> charactersToValues = new HashMap<>();

    private final transient int[] stringLengthsBySize;
    private final transient long[] placeValues;

    /**
     * <p>Constructs a new alphabet codec that uses the given alphabet to encode and decode integers. Note that the
     * order of characters in the given alphabet is significant; swapping the position of two characters in the given
     * alphabet also swaps the values represented by those characters.</p>
     *
     * <p>Legal alphabets must meet certain criteria:</p>
     *
     * <ul>
     *  <li>Alphabets must contain at least two characters</li>
     *  <li>Alphabets must not contain any repeated characters</li>
     * </ul>
     *
     * @param alphabet the alphabet to use when encoding or decoding integers
     *
     * @throws IllegalArgumentException if the given alphabet is too short or contains repeated characters
     *
     * @see com.eatthepath.idobfuscator.util.AlphabetBuilder
     */
    public AlphabetCodec(final char... alphabet) {
        Objects.requireNonNull(alphabet, "Alphabet must not be null.");

        if (alphabet.length < 2) {
            throw new IllegalArgumentException("Alphabet must contain at least two characters");
        }

        for (int i = 0; i < alphabet.length; i++) {
            for (int j = i + 1; j < alphabet.length; j++) {
                if (alphabet[i] == alphabet[j]) {
                    throw new IllegalArgumentException(String.format("Alphabet contains character '%s' more than once", alphabet[i]));
                }
            }
        }

        this.alphabet = alphabet;

        for (int i = 0; i < alphabet.length; i++) {
            this.charactersToValues.put(alphabet[i], i);
        }

        // Based on the size of the alphabet and the width of an integer, we can determine the maximum length of a
        // string needed to represent any integer value with the given alphabet. With that, we can both perform some
        // low-cost error-checking when we try to decode strings and also pre-calculate place values to avoid repeating
        // work when decoding.
        this.stringLengthsBySize = new int[Long.SIZE + 1];
        {
            this.stringLengthsBySize[0] = 0;

            final double logAlphabetLength = Math.log(this.alphabet.length);

            for (int size = 1; size < this.stringLengthsBySize.length; size++) {
                this.stringLengthsBySize[size] = (int) Math.ceil(Math.log(Math.pow(2, size)) / logAlphabetLength);
            }
        }

        // We only need as many place values as can be represented by the longest possible string for this alphabet
        this.placeValues = new long[this.stringLengthsBySize[this.stringLengthsBySize.length - 1]];
        this.placeValues[0] = 1;

        for (int i = 1; i < this.placeValues.length; i++) {
            this.placeValues[i] = this.placeValues[i - 1] * this.alphabet.length;
        }
    }

    /**
     * Encodes the given integer as a string using this codec's alphabet.
     *
     * @param i the integer to encode
     *
     * @return a string representation of the given integer
     *
     * @throws IllegalArgumentException if the given integer cannot be expressed with {@code nBits} bits
     */
    @Override
    public String encodeIntegerAsString(final long i, final int nBits) {
        BitwiseOperationUtil.assertValueFitsWithinSize(i, nBits);

        long workingCopy = BitwiseOperationUtil.getLowestBits(i, nBits);

        final char[] encodedCharacters = new char[this.stringLengthsBySize[nBits]];

        for (int j = encodedCharacters.length - 1; j >= 0; j--) {
            encodedCharacters[j] = this.alphabet[(int) ArithmicUtils.remainderUnsigned(workingCopy, this.alphabet.length)];
            workingCopy = ArithmicUtils.divideUnsigned(workingCopy, this.alphabet.length);
        }

        return new String(encodedCharacters);
    }

    /**
     * Decodes the given string as an integer.
     *
     * @param string the string to decode as an integer
     *
     * @return the integer represented by the given string
     *
     * @throws IllegalArgumentException if the given string is too long to represent a valid integer using this codec's
     * alphabet, or if the given string contains characters not in this codec's alphabet
     */
    @Override
    public long decodeStringAsInteger(final String string, final int nBits) {
        if (string.length() > this.stringLengthsBySize[nBits]) {
            throw new IllegalArgumentException(
                    String.format("String \"%s\" is too long to represent a valid %d-bit integer in this codec's alphabet.", string, nBits));
        }

        final char[] chars = string.toCharArray();
        long decoded = 0;
        int place = chars.length - 1;

        for (final char c : chars) {
            try {
                decoded += this.charactersToValues.get(c) * this.placeValues[place--];
            } catch (final NullPointerException e) {
                throw new IllegalArgumentException(
                        String.format("Could not decode \"%s\"; character '%s' not in codec alphabet.", string, c));
            }
        }

        return BitwiseOperationUtil.signExtendLowestBitsToLong(decoded, nBits);
    }
}
