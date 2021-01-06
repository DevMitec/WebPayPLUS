from AESEncryption import AES128Encryption


if __name__ == "__main__":

    key   = "1B7F7958A1D86DCA80D860E78775F0CD"
    texto = "Mensaje"

    aes_encryptor = AES128Encryption()
    ciphertext = aes_encryptor.encrypt(texto , key)
    print( ciphertext )
    plaintext = aes_encryptor.decrypt(key , ciphertext)
    print( plaintext )

