package com.mit.webpayplus;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;
 
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * @author MIT via Esteban "Doc" Garcia Luna
 * @version 1.0
 * @date 2017/10/10
 */
 
public class AESCrypto {
	private static final String ALGORITMO = "AES/CBC/PKCS5Padding"; // Este es el algoritmo a usar
	private static final String CODIFICACION = "UTF-8"; //Esta es la codificación a usar
	
	/**
	 * Permite cifrar una cadena a partir de un llave proporcionada
	 * @param strToEncrypt
	 * @param key
	 * @return String con la cadena encriptada
	 */
 
    public static String encrypt(String strToEncrypt, String key)
    {
        try
        {
        	byte[] raw = DatatypeConverter.parseHexBinary(key);
        	SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] iv = cipher.getIV();
            byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes(CODIFICACION));
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    		outputStream.write(iv);
    		outputStream.write(cipherText);
            
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            String error = "Error al cifrar: " + e.toString();
            return error;
        }
    }
 
    
    /**
	 * Permite descifrar una cadena a partir de un llave proporcionada
	 * @param strToDecrypt
	 * @param key
	 * @return String con la cadena descifrada
	 */
    public static String decrypt(String strToDecrypt, String key)
    {
        try
        {
        	byte[] encryptedData = DatatypeConverter
    				.parseBase64Binary(strToDecrypt);

    		byte[] raw = DatatypeConverter.parseHexBinary(key);

    		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

    		byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);

    		byte[] cipherText = Arrays.copyOfRange(encryptedData, 16,
    				encryptedData.length);
    		IvParameterSpec iv_specs = new IvParameterSpec(iv);

    		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv_specs);

    		byte[] plainTextBytes = cipher.doFinal(cipherText);
    		String plainText = new String(plainTextBytes);

    		return plainText;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            String error = "Error al descifrar: " + e.toString();
            return error;
        }
    }
}


