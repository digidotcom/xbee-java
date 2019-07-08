/*
 * Copyright 2019, Digi International Inc.
 *
 * Porting of NETSRP.
 *
 * NETSRP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NETSRP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.digi.xbee.api.utils.srp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Utility class for the Secure Remote Password protocol.
 *
 * @since 1.3.0
 */
public class SrpUtils {

	/**
	 * Generates and returns a random salt number.
	 *
	 * @return Random salt number.
	 */
	public static byte[] generateSalt() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] salt = new byte[SrpConstants.LENGTH_SALT];
		secureRandom.nextBytes(salt);
		return salt;
	}

	/**
	 * Generates and returns a verifier from the provided salt and password.
	 *
	 * <p>Note that the username (I) in the XBee Bluetooth Low Energy API is
	 * fixed to {@code apiservice}.</p>
	 *
	 * @param salt The salt to use.
	 * @param password The user password.
	 *
	 * @return A new SRP verifier.
	 * 
	 * @throws IOException If there is a problem generating the X value.
	 * @throws NoSuchAlgorithmException If the hash algorithm to use does not
	 *                                  exist.
	 */
	public static byte[] generateVerifier(byte[] salt, String password) throws IOException, NoSuchAlgorithmException {
		BigInteger x = bigIntegerFromBytes(generateX(salt, SrpConstants.API_USERNAME.getBytes(), password.getBytes()));
		byte[] verifier = new byte[SrpConstants.LENGTH_VERIFIER];
		byte[] v = bigIntegerToBytes(SrpConstants.g.modPow(x, SrpConstants.N));
		// Make sure the verifier is 128 bytes.
		System.arraycopy(v, 0, verifier, verifier.length - v.length, v.length);
		return verifier;
	}

	/**
	 * Generates the X value as dictated by the SRP spec.
	 *
	 * @param byte_s Salt.
	 * @param byte_I Username.
	 * @param byte_p Password.
	 *
	 * @return The X value.
	 * 
	 * @throws IOException If there is a problem generating the X value.
	 * @throws NoSuchAlgorithmException If the hash algorithm to use does not
	 *                                  exist.
	 */
	static byte[] generateX(byte[] byte_s, byte[] byte_I, byte[] byte_p) throws NoSuchAlgorithmException, IOException {
		byte[] byte_x;

		MessageDigest digest = MessageDigest.getInstance(SrpConstants.HASH_ALGORITHM);

		// Concat username and pw.
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(byte_I);
		os.write(SrpConstants.SEPARATOR);
		os.write(byte_p);
		byte[] userPwHash = digest.digest(os.toByteArray());

		// Hash along the salt and then the hashed username + pw.
		os = new ByteArrayOutputStream();
		os.write(byte_s);
		os.write(userPwHash);
		byte_x = digest.digest(os.toByteArray());

		return byte_x;
	}

	/**
	 * Generate the M value as per the SRP spec.
	 *
	 * @param byte_N Large prime number.
	 * @param byte_g Generator number.
	 * @param byte_I Username.
	 * @param byte_s Salt.
	 * @param byte_A A as per the SRP spec.
	 * @param byte_B B as per the SRP spec.
	 * @param byte_K K as per the SRP spec.
	 *
	 * @return The M value.
	 * 
	 * @throws IOException If there is a problem generating the M value.
	 * @throws NoSuchAlgorithmException If the hash algorithm to use does not
	 *                                  exist.
	 */
	static byte[] generateM(byte[] byte_N, byte[] byte_g, byte[] byte_I, byte[] byte_s,
			byte[] byte_A, byte[] byte_B, byte[] byte_K) throws NoSuchAlgorithmException, IOException {
		byte[] byte_M;

		MessageDigest digest = MessageDigest.getInstance(SrpConstants.HASH_ALGORITHM);

		byte[] byte_Nxorh = hashXor(byte_N, byte_g);
		byte[] byte_Ih = digest.digest(byte_I);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(byte_Nxorh);
		os.write(byte_Ih);
		os.write(byte_s);
		os.write(byte_A);
		os.write(byte_B);
		os.write(byte_K);
		byte_M = digest.digest(os.toByteArray());

		return byte_M;
	}

	/**
	 * Hashes and xor the passed byte arrays.
	 *
	 * @param byte_1 Byte array 1.
	 * @param byte_2 Byte array 2.
	 *
	 * @return Hashed and xor of the given arrays.
	 * 
	 * @throws NoSuchAlgorithmException If the hash algorithm to use does not
	 *                                  exist.
	 */
	private static byte[] hashXor(byte[] byte_1, byte[] byte_2) throws NoSuchAlgorithmException {
		byte[] ret;

		MessageDigest digest = MessageDigest.getInstance(SrpConstants.HASH_ALGORITHM);

		// Hash both byte arrays.
		byte_1 = digest.digest(byte_1);
		byte_2 = digest.digest(byte_2);

		// Xor them together.
		ret = new byte[byte_1.length];
		for (int i = 0; i < byte_1.length; i++)
			ret[i] = (byte) (byte_1[i] ^ byte_2[i]);

		return ret;
	}

	/**
	 * Converts the given byte array into a BigInteger object.
	 *
	 * @param bytes Byte array.
	 *
	 * @return BigInteger object.
	 */
	static BigInteger bigIntegerFromBytes(byte[] bytes) {
		return new BigInteger(1, bytes);
	}

	/**
	 * Converts the given BigInteger object into a byte array.
	 *
	 * @param bigInteger BigInteger object.
	 *
	 * @return Byte array.
	 */
	static byte[] bigIntegerToBytes(BigInteger bigInteger) {
		byte[] bytes = bigInteger.toByteArray();
		if (bytes[0] == 0)
			return Arrays.copyOfRange(bytes, 1, bytes.length);
		return bytes;
	}

}
