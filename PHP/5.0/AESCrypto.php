<?php

class AESCrypto {
    
    //private const OPENSSL_CIPHER_NAME = "aes-128-cbc";
    //private const CIPHER_KEY_LEN = 16; //128 bits
    //define('CIPHER_KEY_LEN', "16");
    //define('OPENSSL_CIPHER_NAME', 'aes-128-cbc');
    
    private static function fixKey($key) {
        
        if (strlen($key) < 16) {
            //0 pad to len 16
            return str_pad("$key", 16, "0"); 
        }
        
        if (strlen($key) > 16) {
            //truncate to 16 bytes
            return substr($key, 0, 16); 
        }

        return $key;
    }

    /**
    * Encrypt data using AES Cipher (CBC) with 128 bit key
    * 
    * @param type $key - key to use should be 16 bytes long (128 bits)
    * @param type $iv - initialization vector
    * @param type $data - data to encrypt
    * @return encrypted data in base64 encoding with iv attached at end after a :
    */
    static function encrypt($key, $data) {
        $ivlen = openssl_cipher_iv_length('AES-128-CBC');
        $iv = openssl_random_pseudo_bytes($ivlen);
        $encodedEncryptedData = base64_encode(openssl_encrypt($data, 'aes-128-cbc', AesCipher::fixKey($key), OPENSSL_RAW_DATA, $iv));
        $encodedIV = base64_encode($iv);
        $encryptedPayload = $encodedEncryptedData.":".$encodedIV;

        return base64_encode($encryptedPayload);
    }

    /**
    * Decrypt data using AES Cipher (CBC) with 128 bit key
    * 
    * @param type $key - key to use should be 16 bytes long (128 bits)
    * @param type $data - data to be decrypted in base64 encoding with iv attached at the end after a :
    * @return decrypted data
    */
    static function decrypt($key, $data) {
		$data = base64_decode($data);
        $parts = explode(':', $data); //Separate Encrypted data from iv.
        $encrypted = $parts[0];
        $iv = $parts[1];
        //$ivlen = openssl_cipher_iv_length('AES-128-CBC');
        //$iv = openssl_random_pseudo_bytes($ivlen);
        $decryptedData = openssl_decrypt(base64_decode($encrypted), 'aes-128-cbc', AesCipher::fixKey($key), OPENSSL_RAW_DATA, base64_decode($iv));

        return $decryptedData;
    }
};
echo '<br>';
//$decrypted = AesCipher::decrypt('0123456789abcdef', 'G0a6cK+sxBkSwyCjcG4efA==:YWJjZGVmOTg3NjU0MzIxMA==');
$encrypted = AesCipher::encrypt('0123456789abcdef','pruebadesdephp');
var_dump($encrypted);
$decrypted = AesCipher::decrypt('0123456789abcdef', 'K1R2MG9aOWRuSWdPajAwVUJvajlidz09OnlIYm5ObEkzcGZZREw3a3V4VVNXdHc9PQ==');
echo '<br>';
var_dump($decrypted);

?>