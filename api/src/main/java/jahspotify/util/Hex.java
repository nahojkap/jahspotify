package jahspotify.util;
/**
 * Class providing methods to convert between hexadecimal strings and byte arrays.
 * 
 * @author Felix Bruns <felixbruns@web.de>
 */
public class Hex {
	/**
	 * Convert a hexadecimal string into a byte array.
	 * <ul>
	 * 	<li>Safe with leading zeroes (unlike {@link java.math.BigInteger}).</li>
	 * 	<li>Safe with negative byte values (unlike {@link Byte}.parseByte).</li>
	 * </ul>
	 * 
	 * @param hex The hexadecimal string to convert.
	 * 
	 * @return A byte array
	 */
	public static byte[] toBytes(String hex){
		if(!isHex(hex)){
			throw new IllegalArgumentException("Input string is not a valid hexadecimal string!");
		}
		
		byte[] bytes = new byte[hex.length() / 2];
		
		/* Loop over two characters at a time and convert them to a byte. */
		for(int i = 0; i < hex.length(); i += 2){
			bytes[i / 2] = (byte)(
				(Character.digit(hex.charAt(i    ), 16) << 4) +
				 Character.digit(hex.charAt(i + 1), 16)
			);
		}
		
		return bytes;
	}
	
	/**
	 * Convert a byte array into a hexadecimal string.
	 * 
	 * @param bytes The byte array
	 * 
	 * @return A hexadecimal string
	 */
	public static String toHex(byte[] bytes){
		String hex = "";
		
		for(int i = 0; i < bytes.length; i++){
			hex += String.format("%02x", bytes[i]);
		}
		
		return hex;
	}
	
	/**
	 * Check if a string is a valid hexadecimal string.
	 * 
	 * @param hex The string to check.
	 * 
	 * @return true if it is a valid hexadecimal string, false otherwise.
	 */
	public static boolean isHex(String hex){
		return (hex.length() % 2 == 0) && hex.matches("[0-9A-Fa-f]+");
	}
}
