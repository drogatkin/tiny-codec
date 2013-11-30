package jwbroek.util;

public class CharsetUtil {
	public static boolean matchUTF8(byte[] chars) {
		return  matchUTF8(chars, 0, chars.length);
	}

	public static boolean matchUTF8(byte[] chars, int pos, int len) {
		if (chars != null && pos >=0 && pos <= chars.length-len)
			for (int i = pos; i < pos+len; i++) {
				byte b = chars[i];
				int n;
				if ((b & 255) < 0x80)
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
				//System.out.printf("0%x - %d%n", b, n);
				for (int j = 0; j < n; j++) { // n bytes matching 10bbbbbb follow ?
					if ((++i == pos+len) || ((chars[i] & 0xC0) != 0x80))
						return false;
				}
				if (n > 0)
					return true;
			}
		return false;
	}
}
