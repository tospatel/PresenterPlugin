package presenter.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class Base64Util {
	private Base64Util() {

	}

	/**
	 * Decode encoding string
	 * 
	 * @param s
	 * @return
	 */
	public static String decode(String s) {
		return StringUtils.newStringUtf8(Base64.decodeBase64(s));
	}

	/**
	 * Encode decoded string
	 * 
	 * @param s
	 * @return
	 */
	public static String encode(String s) {
		return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
	}
}
