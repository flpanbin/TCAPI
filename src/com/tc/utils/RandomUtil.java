package com.tc.utils;

import java.util.Random;
import java.util.UUID;

public class RandomUtil {
	/**
	 * Array of numbers and letters of mixed case. Numbers appear in the list
	 * twice so that there is a more equal chance that a number will be picked.
	 * We can use the array to get a random number or letter by picking a random
	 * array index.
	 */
	private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
			+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

	/**
	 * Returns a random String of numbers and letters (lower and upper case) of
	 * the specified length. The method uses the Random class that is built-in
	 * to Java which is suitable for low to medium grade security uses. This
	 * means that the output is only pseudo random, i.e., each number is
	 * mathematically generated so is not truly random.
	 * <p>
	 * <p/>
	 * The specified length must be at least one. If not, the method will return
	 * null.
	 *
	 * @param length
	 *            the desired length of the random String to return.
	 * @return a random String of numbers and letters of the specified length.
	 */
	public static String randomString(int length) {
		if (length < 1) {
			return null;
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[new Random().nextInt(71)];
		}
		return new String(randBuffer);
	}

	/**
	 * 生成32位UUID字符，去除字符'-'
	 * 
	 * @return 32位随机UUID字符串
	 */
	public static String randomCustomUUID() {
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString();

		return uuidStr.replaceAll("-", "");
	}
}
