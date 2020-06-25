using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography;
using System.IO;


//
// @author MIT
// @version 1.0
// @date 2017/10/10
//


namespace webpayplus
{
    static public class AESCrypto
    {
        public string encrypt(string strToEncrypt, string strKey)
        {
            try
            {
                string original = strToEncrypt;

                // Create a new instance of the Aes
                // class.  This generates a new key and initialization 
                // vector (IV).
                using (Aes myAes = Aes.Create())
                {
                    myAes.Mode = CipherMode.CBC;
                    myAes.KeySize = 128;
                    myAes.Padding = PaddingMode.PKCS7;
                    myAes.BlockSize = 128;
                    myAes.FeedbackSize = 128;
                    byte[] key = new byte[] { };
                    String result = "";

                    string str = strKey;
                    key = StringToByteArray(str);
                    myAes.Key = key;

                    // Encrypt the string to an array of bytes.
                    byte[] encrypted = EncryptStringToBytes_Aes128(original, myAes.Key, myAes.IV);

                    byte[] resultado = new byte[encrypted.Length + myAes.IV.Length];
                    Array.Copy(myAes.IV, 0, resultado, 0, myAes.IV.Length);
                    Array.Copy(encrypted, 0, resultado, myAes.IV.Length, encrypted.Length);
                    return result = System.Convert.ToBase64String(resultado);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: {0}", e.Message);
                return "";
            }
        }

        public string decrypt(string strKey, string strToDecrypt)
        {
            try
            {
                string original = strToDecrypt;

                // Create a new instance of the Aes
                // class.  This generates a new key and initialization 
                // vector (IV).
                using (Aes myAes = Aes.Create())
                {
                    myAes.Mode = CipherMode.CBC;
                    myAes.KeySize = 128;
                    myAes.Padding = PaddingMode.PKCS7;
                    myAes.BlockSize = 128;
                    myAes.FeedbackSize = 128;
                    byte[] key = new byte[] { };
                    byte[] xmlByte = new byte[] { };
                    String result = "";

                    string str = strKey;
                    key = StringToByteArray(str);

                    var base64EncodedBytes = System.Convert.FromBase64String(strToDecrypt);
                    byte[] IVAES128 = new byte[16];
                    Array.Copy(base64EncodedBytes, 0, IVAES128, 0, 16);
                    myAes.IV = IVAES128;

                    base64EncodedBytes = System.Convert.FromBase64String(strToDecrypt);
                    xmlByte = new byte[base64EncodedBytes.Length - 16];
                    Array.Copy(base64EncodedBytes, 16, xmlByte, 0, base64EncodedBytes.Length - 16);
                    myAes.Key = key;

                    // Encrypt the string to an array of bytes.
                    result = DecryptStringFromBytes_Aes128(xmlByte, myAes.Key, myAes.IV);
                    return result;
                }

            }
            catch (Exception e)
            {
                Console.WriteLine("Error: {0}", e.Message);
                return "";
            }
        }

        private static byte[] EncryptStringToBytes_Aes128(string plainText, byte[] Key, byte[] IV)
        {
            // Check arguments.
            if (plainText == null || plainText.Length <= 0)
                throw new ArgumentNullException("plainText");
            if (Key == null || Key.Length <= 0)
                throw new ArgumentNullException("Key");
            if (IV == null || IV.Length <= 0)
                throw new ArgumentNullException("Key");
            byte[] encrypted;
            // Create an Aes object
            // with the specified key and IV.
            using (Aes aesAlg = Aes.Create())
            {
                aesAlg.Mode = CipherMode.CBC;
                aesAlg.KeySize = 128;
                aesAlg.Padding = PaddingMode.PKCS7;
                aesAlg.BlockSize = 128;
                aesAlg.FeedbackSize = 128;
                aesAlg.Key = Key;
                aesAlg.IV = IV;

                // Create a decrytor to perform the stream transform.
                ICryptoTransform encryptor = aesAlg.CreateEncryptor(aesAlg.Key, aesAlg.IV);

                // Create the streams used for encryption.
                using (MemoryStream msEncrypt = new MemoryStream())
                {
                    using (CryptoStream csEncrypt = new CryptoStream(msEncrypt, encryptor, CryptoStreamMode.Write))
                    {
                        using (StreamWriter swEncrypt = new StreamWriter(csEncrypt))
                        {

                            //Write all data to the stream.
                            swEncrypt.Write(plainText);
                        }
                        encrypted = msEncrypt.ToArray();
                    }
                }
            }
            // Return the encrypted bytes from the memory stream.
            return encrypted;
        }

        private static string DecryptStringFromBytes_Aes128(byte[] cipherText, byte[] Key, byte[] IV)
        {
            // Check arguments.
            if (cipherText == null || cipherText.Length <= 0)
                throw new ArgumentNullException("cipherText");
            if (Key == null || Key.Length <= 0)
                throw new ArgumentNullException("Key");
            if (IV == null || IV.Length <= 0)
                throw new ArgumentNullException("Key");

            // Declare the string used to hold
            // the decrypted text.
            string plaintext = null;

            // Create an Aes object
            // with the specified key and IV.
            using (Aes aesAlg = Aes.Create())
            {
                aesAlg.Key = Key;
                aesAlg.IV = IV;
                aesAlg.Mode = CipherMode.CBC;
                aesAlg.KeySize = 128;
                aesAlg.Padding = PaddingMode.PKCS7;
                aesAlg.BlockSize = 128;
                aesAlg.FeedbackSize = 128;
                aesAlg.Key = Key;
                aesAlg.IV = IV;


                // Create a decrytor to perform the stream transform.
                ICryptoTransform decryptor = aesAlg.CreateDecryptor(aesAlg.Key, aesAlg.IV);

                // Create the streams used for decryption.
                using (MemoryStream msDecrypt = new MemoryStream(cipherText))
                {
                    using (CryptoStream csDecrypt = new CryptoStream(msDecrypt, decryptor, CryptoStreamMode.Read))
                    {
                        using (StreamReader srDecrypt = new StreamReader(csDecrypt))
                        {

                            // Read the decrypted bytes from the decrypting stream
                            // and place them in a string.
                            plaintext = srDecrypt.ReadToEnd();
                        }
                    }
                }

            }
            return plaintext;
        }

        private static byte[] StringToByteArray(string hex)
        {
            return Enumerable.Range(0, hex.Length)
                             .Where(x => x % 2 == 0)
                             .Select(x => Convert.ToByte(hex.Substring(x, 2), 16))
                             .ToArray();
        }

        private static string ByteArrayToString(byte[] ba)
        {
            StringBuilder hex = new StringBuilder(ba.Length * 2);
            foreach (byte b in ba)
                hex.AppendFormat("{0:x2}", b);
            return hex.ToString();
        }
    }
}
