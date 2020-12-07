<?php

/**
 * @author Mercadotecnia, Ideas y Tecnologia
 * @version 1.0
 * @date 2017/10/10
 * 
 * En php.ini habilitar la linea extension=php_openssl.dll (o equivalente a linux)
 */

    class AESCrypto{
    
      /**
       * Permite cifrar una cadena a partir de un llave proporcionada
       * @param strToEncrypt
       * @param key
       * @return String con la cadena encriptada
       */

      public static function encriptar($plaintext, $key128){
          $iv = openssl_random_pseudo_bytes(openssl_cipher_iv_length('aes-128-cbc'));
          $cipherText = openssl_encrypt ( $plaintext, 'AES-128-CBC', hex2bin($key128), 1, $iv);
          return base64_encode($iv.$cipherText);
        }


      /**
       * Permite descifrar una cadena a partir de un llave proporcionada
       * @param strToDecrypt
       * @param key
       * @return String con la cadena descifrada
       */

      public static function desencriptar($encodedInitialData, $key128){
        $encodedInitialData =  base64_decode($encodedInitialData);
        $iv = substr($encodedInitialData,0,16);
        $encodedInitialData = substr($encodedInitialData,16);
        $decrypted = openssl_decrypt($encodedInitialData, 'AES-128-CBC', hex2bin($key128), 1, $iv);
        return $decrypted;
      }
}
?>
