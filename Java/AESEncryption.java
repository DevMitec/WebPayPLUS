package com.mit.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.DecoderException;

/**
 * @author apiedra MIT
 * @version 1.0
 * 
 */
public class AESEncryption {
	private static final String ALGORITMO = "AES/CBC/PKCS5Padding";
	private static final String CODIFICACION = "UTF-8";

	/**
	 * Permite encriptar una cadena a partir de un llave proporcionada
	 * @param plaintext
	 * @param key
	 * @return String con la cadena encriptada
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 * @throws DecoderException
	 */
	public static String encrypt(String plaintext, String key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, IOException, DecoderException {

		
		byte[] raw = DatatypeConverter.parseHexBinary(key);

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] cipherText = cipher.doFinal(plaintext.getBytes(CODIFICACION));
		byte[] iv = cipher.getIV();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(cipherText);

		byte[] finalData = outputStream.toByteArray();

		String encodedFinalData = DatatypeConverter
				.printBase64Binary(finalData);

		return encodedFinalData;

	}

	/**
	 * Permite desencriptar una cadena a partir de la llave proporcionada
	 * @param encodedInitialData
	 * @param key
	 * @return String de la cadena en claro
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws DecoderException
	 */
	public static String decrypt(String encodedInitialData, String key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, DecoderException {

		byte[] encryptedData = DatatypeConverter
				.parseBase64Binary(encodedInitialData);

		byte[] raw = DatatypeConverter.parseHexBinary(key);

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);

		byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);

		byte[] cipherText = Arrays.copyOfRange(encryptedData, 16,
				encryptedData.length);
		IvParameterSpec iv_specs = new IvParameterSpec(iv);

		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv_specs);

		byte[] plainTextBytes = cipher.doFinal(cipherText);
		String plainText = new String(plainTextBytes);

		return plainText;
	}

	public static void main(String args[]) throws Exception {
		String key = "605bd70efed2c6374823b54bbc560b58"; // LLAVE DE CIFRADO, ES UN DATO VARIABLE Y SERA PROPORCIONADO POR MIT 
		String plaintext = "Cadena a encriptar XML";

		System.out.println("Encrypt:" + encrypt(plaintext, key));
		System.out.println("Decrypt:" + decrypt(encrypt(plaintext, key), key));		
	}

}
