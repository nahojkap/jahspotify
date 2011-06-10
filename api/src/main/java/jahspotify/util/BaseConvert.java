package jahspotify.util;

import java.math.BigInteger;

/**
 * Class providing methods for converting between different radices.
 * 
 * @author Felix Bruns <felixbruns@web.de>
 */
public class BaseConvert {
	/**
	 * The minimum radix available for conversion to and from strings. The
	 * constant value of this field is the smallest value permitted for the
	 * radix argument in radix-conversion methods such as the digit method
	 * and the {@code forDigit} method.
	 */
	public static final int MIN_RADIX = 2;
	
	/**
	 * The maximum radix available for conversion to and from strings. The
	 * constant value of this field is the largest value permitted for the
	 * radix argument in radix-conversion methods such as the digit method
	 * and the {@code forDigit} method.  
	 */
	public static final int MAX_RADIX = 62;
	
	/**
	 * A string holding the available characters for conversion to and from
	 * strings.
	 */
	private static final String CHARACTERS =
		"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/**
	 * Returns the numeric value of the character ''c'' in the specified radix.
	 * <br><br>
	 * If the radix is not in the range {@code MIN_RADIX <= radix <= MAX_RADIX}
	 * or if the value of {@code ch} is not a valid digit in the specified
	 * radix, -1 is returned.
	 * 
	 * @param ch    the character to be converted.
	 * @param radix the radix.
	 * 
	 * @return the numeric value represented by the character in the specified radix.
	 */
	public static int digit(char ch, int radix){
		/* 
		 * If the radix is less or equal to 36, we automatically convert upper-case
		 * characters to lower-case. Otherwise upper-case characters won't be valid
		 * in the specified radix, but we still want them to be valid (for example
		 * when using hexadecimal numbers).
		 */
		if(radix <= 36){
			ch = Character.toLowerCase(ch);
		}
		
		/* The index of our character is the digit we want. */
		return CHARACTERS.substring(0, radix).indexOf(ch);
	}
	
	/**
	 * Determines the character representation for a specific digit in the
	 * specified radix. If the value of radix is not a valid radix, or the
	 * value of digit is not a valid digit in the specified radix, the null
	 * character ('\u0000') is returned.
	 * <br><br>
	 * The radix argument is valid if it is greater than or equal to MIN_RADIX
	 * and less than or equal to MAX_RADIX. The digit argument is valid if
	 * {@code 0 <= digit < radix}.
	 * 
	 * @param digit the number to convert to a character.
	 * @param radix the radix.
	 * 
	 * @return the char representation of the specified digit in the specified radix.
	 */
	public static char forDigit(int digit, int radix){
		/* The character at the index equal to the digit is the one we want. */
		return CHARACTERS.substring(0, radix).charAt(digit);
	}
	
	/**
	 * Converts a string from the source radix to a string in the target radix.
	 * 
	 * @param source      the source string to be converted.
	 * @param sourceRadix the source radix.
	 * @param targetRadix the target radix.
	 * 
	 * @return the result string in the target radix.
	 */
	public static String convert(String source, int sourceRadix, int targetRadix){
		/* Create a StringBuilder for building the result string. */
		StringBuilder result = new StringBuilder();
		
		/* Check if radix arguments are within the allowed range. */
		if(sourceRadix < MIN_RADIX || targetRadix < MIN_RADIX ||
			sourceRadix > MAX_RADIX || targetRadix > MAX_RADIX){
			throw new IllegalArgumentException(
				"Source and target radix both need to be in a" +
				" range from " + MIN_RADIX + " to " + MAX_RADIX
			);
		}
		
		/* Create BigInteger objects that we need for conversion. */
		BigInteger radixFrom  = BigInteger.valueOf(sourceRadix);
		BigInteger radixTo    = BigInteger.valueOf(targetRadix);
	    BigInteger value      = BigInteger.ZERO;
	    BigInteger multiplier = BigInteger.ONE;
		
	    /* Loop over source string and convert it to a number using the source radix. */
	    for(int i = source.length() - 1; i >= 0; i--){
	    	/* Get digit for character in source radix. */
	    	int digit = digit(source.charAt(i), sourceRadix);
	    	
	    	/* If digit is not defined, it's an illegal character. */
	    	if(digit == -1){
	    		throw new IllegalArgumentException(
	    			"The character '" + source.charAt(i) + "'" +
	    			" is not defined for the radix " + sourceRadix
	    		);
	    	}
	    	
	    	/* Add value of character (digit) multiplied with the current multiplier to value. */
	    	value = value.add(multiplier.multiply(BigInteger.valueOf(digit)));
	    	
	    	/* Calculate multiplier for next (higher) character. */
			multiplier = multiplier.multiply(radixFrom);
	    }
	    
	    /* Convert number to a string using the target radix. */
	    while(BigInteger.ZERO.compareTo(value) < 0){
	    	/* Divide value by target radix and also get the remainder. */
	    	BigInteger[] quotientAndRemainder = value.divideAndRemainder(radixTo);
	    	
	    	/* Get character for digit (remainder) in target radix. */
	    	char c = forDigit(quotientAndRemainder[1].intValue(), targetRadix);
	    	
	    	/* Prepend character to result. */
	    	result.insert(0, c);
	    	
	    	/* Set new value to quotient. */
	    	value = quotientAndRemainder[0];
	    }
	    
	    /* Return result string which is now in target radix. */
		return result.toString();
	}
}
