package jwbroek.util;

public class CharsetUtil {
	public static boolean matchUTF8(byte[] chars) {
		for (int i = 0; i < chars.length; i++) {
			byte b = chars[i];
			int n;
			if (b < 0x80)
				n = 0; // 0bbbbbbb
			else if ((b & 0xE0) == 0xC0)
				n = 1; // 110bbbbb
			else if ((b & 0xF0) == 0xE0)
				n = 2; // 1110bbbb
			else if ((b & 0xF8) == 0xF0)
				n = 3; // 11110bbb
			else if ((b & 0xFC) == 0xF8)
				n = 4; // 111110bb
			else if ((b & 0xFE) == 0xFC)
				n = 5; // 1111110b
			else
				return false; // Does not match any model
			for (int j = 0; j < n; j++) { // n bytes matching 10bbbbbb follow ?
				if ((++i == chars.length) || ((chars[i] & 0xC0) != 0x80))
					return false;
			}
			if (n > 0)
				return true;
		}
		return false;
	}
}
