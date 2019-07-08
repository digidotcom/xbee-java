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

/**
 * This class represents the user role in the Secure Remote Password protocol.
 *
 * @since 1.3.0
 */
public class SrpUser {

	private String username;
	private String password;

	private boolean authenticated = false;

	private BigInteger N;
	private BigInteger g;
	private BigInteger a;
	private byte[] byte_A;
	private BigInteger k;
	private byte[] H_AMK;
	private byte[] byte_K;

	/**
	 * Class constructor. Instantiates a new {@code SrpUser} object with the
	 * given parameters.
	 *
	 * @param username The username to authenticate.
	 * @param password The password to authenticate.
	 */
	public SrpUser(String username, String password) {
		this(username, password, null, null);
	}

	/**
	 * Class constructor. Instantiates a new {@code SrpUser} object with the
	 * given parameters.
	 *
	 * @param username The username to authenticate.
	 * @param password The password to authenticate.
	 * @param byte_N N as per the SRP formula. If not passed, a default 1024 bit
	 *               N is used.
	 * @param byte_g The corresponding generator value for N, by default, 2.
	 */
	private SrpUser(String username, String password, byte[] byte_N, byte[] byte_g) {
		this.username = username;
		this.password = password;
		N = byte_N != null ? SrpUtils.bigIntegerFromBytes(byte_N) : SrpConstants.N;
		g = byte_g != null ? SrpUtils.bigIntegerFromBytes(byte_g) : SrpConstants.g;
	}

	/**
	 * Computes and returns A, which is needed to start authentication.
	 *
	 * @return Public ephemeral value A.
	 * 
	 * @throws IOException If there is a problem generating 'byte k' value.
	 * @throws NoSuchAlgorithmException If the hash algorithm to use does not
	 *                                  exist.
	 */
	public byte[] startAuthentication() throws NoSuchAlgorithmException, IOException {
		// Reset variables.
		authenticated = false;
		byte_K = null;

		// Generate a random 32 byte a.
		byte[] byte_a = new byte[32];
		new SecureRandom().nextBytes(byte_a);
		this.a = SrpUtils.bigIntegerFromBytes(byte_a);

		// Compute A.
		byte_A = SrpUtils.bigIntegerToBytes(g.modPow(a, N));

		// Compute k in byte array form.
		byte[] byte_k;
		MessageDigest digest = MessageDigest.getInstance(SrpConstants.HASH_ALGORITHM);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(SrpUtils.bigIntegerToBytes(N));
		os.write(SrpUtils.bigIntegerToBytes(g));
		byte_k = digest.digest(os.toByteArray());

		// Get BigInteger k and store it.
		this.k = SrpUtils.bigIntegerFromBytes(byte_k);

		return byte_A;
	}

	/**
	 * Returns M1 if the challenge was successfully processed.
	 *
	 * @param byte_s Salt.
	 * @param byte_B Public ephemeral value B.
	 *
	 * @return Proof of session key M1, or {@code null} if the challenge could
	 *         not be processed.
	 * 
	 * @throws IOException If there is a problem generating any value.
	 * @throws NoSuchAlgorithmException If the hash algorithm to use does not
	 *                                  exist.
	 */
	public byte[] processChallenge(byte[] byte_s, byte[] byte_B) throws NoSuchAlgorithmException, IOException {
		BigInteger B = SrpUtils.bigIntegerFromBytes(byte_B);

		// SRP-6a dictated safety check.
		if (B.mod(N).equals(BigInteger.ZERO))
			return null;

		// Compute M.
		byte[] byte_M;

		MessageDigest digest = MessageDigest.getInstance(SrpConstants.HASH_ALGORITHM);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(byte_A);
		os.write(SrpUtils.bigIntegerToBytes(B));
		byte[] byte_u = digest.digest(os.toByteArray());

		BigInteger u = SrpUtils.bigIntegerFromBytes(byte_u);

		// SRP-6a dictated safety check.
		if (u.equals(BigInteger.ZERO))
			return null;

		// Compute x.
		byte[] byte_I = username.getBytes();
		byte[] byte_p = password.getBytes();
		byte[] byte_x = SrpUtils.generateX(byte_s, byte_I, byte_p);
		BigInteger x = SrpUtils.bigIntegerFromBytes(byte_x);

		// Compute v.
		BigInteger v = g.modPow(x, N).abs();

		// Compute S.
		// The remainder is computed here, not the modulo.
		// This means that, if n is negative, we need to do N - remainder to get the modulo.
		BigInteger S = B.subtract(k.multiply(v)).modPow(a.add(u.multiply(x)), N);

		if (S.compareTo(BigInteger.ZERO) < 0)
			S = N.add(S);

		// Compute K.
		byte_K = digest.digest(SrpUtils.bigIntegerToBytes(S));

		// Compute M.
		byte_M = SrpUtils.generateM(SrpUtils.bigIntegerToBytes(N), SrpUtils.bigIntegerToBytes(g), byte_I,
				byte_s, byte_A, SrpUtils.bigIntegerToBytes(B), byte_K);

		// And finally, hash A, M and K together.
		os = new ByteArrayOutputStream();
		os.write(byte_A);
		os.write(byte_M);
		os.write(byte_K);
		H_AMK = digest.digest(os.toByteArray());

		return byte_M;
	}

	/**
	 * Verifies the given server proof of session key M2.
	 *
	 * @param host_H_AMK Proof of session key M2.
	 */
	public void verifySession(byte[] host_H_AMK) {
		if (host_H_AMK.length != H_AMK.length)
			return;

		for (int i = 0; i < H_AMK.length; i++) {
			if (H_AMK[i] != host_H_AMK[i])
				return;
		}

		authenticated = true;
	}

	/**
	 * Returns whether the user is authenticated or not.
	 *
	 * @return {@code true} if the user is authenticated, {@code false}
	 *         otherwise.
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * Returns the session key.
	 *
	 * @return The session key, or {@code null} if it is not known yet.
	 */
	public byte[] getSessionKey() {
		return byte_K;
	}

}
