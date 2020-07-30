import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypto {

	private static String CIPHER_NAME = "AES/CBC/PKCS5PADDING";

	private static int CIPHER_KEY_LEN = 16; // 128 bits

	/**
	 * Encrypt data using AES Cipher (CBC) with 128 bit key
	 *
	 * @param key  - key to use should be 16 bytes long (128 bits)
	 * @param data - data to encrypt
	 * @return encryptedData data in base64 encoding with iv attached at end after a
	 *         :
	 */
	public static String encrypt(String key, String data) {

		try {
			SecureRandom random = new SecureRandom();
			byte[] ivArr = random.generateSeed(16);
			IvParameterSpec ivSpec = new IvParameterSpec(ivArr);
			SecretKeySpec secretKey = new SecretKeySpec(fixKey(key).getBytes(StandardCharsets.UTF_8), "AES");

			Cipher cipher = Cipher.getInstance(CIPHER_NAME);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

			byte[] encryptedData = cipher.doFinal((data.getBytes()));

			String encryptedDataInBase64 = Base64.getEncoder().encodeToString(encryptedData);
			String ivInBase64 = Base64.getEncoder().encodeToString(ivArr);
			String resultEnc = encryptedDataInBase64 + ":" + ivInBase64;

			return Base64.getEncoder().encodeToString(resultEnc.getBytes());

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String fixKey(String key) {

		if (key.length() < CIPHER_KEY_LEN) {
			int numPad = CIPHER_KEY_LEN - key.length();

			for (int i = 0; i < numPad; i++) {
				key += "0"; // 0 pad to len 16 bytes
			}

			return key;

		}

		if (key.length() > CIPHER_KEY_LEN) {
			return key.substring(0, CIPHER_KEY_LEN); // truncate to 16 bytes
		}

		return key;
	}

	/**
	 * Decrypt data using AES Cipher (CBC) with 128 bit key
	 *
	 * @param key  - key to use should be 16 bytes long (128 bits)
	 * @param data - encrypted data with iv at the end separate by :
	 * @return decrypted data string
	 */

	public static String decrypt(String key, String data) {

		try {
			String cadena = new String(Base64.getDecoder().decode(data));
			data = cadena;
			String[] parts = data.split(":");

			byte[] decodedEncryptedData = Base64.getDecoder().decode(parts[0]);

			byte[] ivBytes = Arrays.copyOfRange(decodedEncryptedData, 0, 16);
			Cipher cipher = Cipher.getInstance(CIPHER_NAME);
			// IvParameterSpec(ivBytes);//Base64.getDecoder().decode(parts[1]));
			IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(parts[1]));
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

			byte[] original = cipher.doFinal(decodedEncryptedData);

			return new String(original);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void main(String[] args) {

		String key = "0123456789abcdef"; // 128 bit key

		String plain_text = "pruebadesdejava123";
		String encrypted = encrypt(key, plain_text);
		System.out.println(encrypted);

		System.out.println("----");
		String decrypt = decrypt(key,
				"Q3lMbnVxalAzK0p2SUY5MGVTaWJsNmtYdkhBazFjcHZMaGNxQ0JsUm1Gaz06Tk1SSjZ5bVl6bFF4TnluQjZnWWpKQT09");
		System.out.println(decrypt);
	}
}