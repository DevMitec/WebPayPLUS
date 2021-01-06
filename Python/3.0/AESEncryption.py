#!/usr/bin/python
# -*- coding: utf-8 -*-

# Biblioteca para cifrado y descifrado de informacion con la biblioteca cryptography
# arodriguez MIT
# version 1.1
# Compatible con Python >= 3.0
# Basado en el modulo cryptography. Se instala con: pip install cryptography
# Documentacion cryptography https://cryptography.io/
# Diciembre 2020

import os
import logging
import base64

from cryptography.hazmat.primitives.ciphers.modes import CBC
from cryptography.hazmat.primitives.ciphers.algorithms import AES
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

#logging.basicConfig(level=#logging.INFO)


class AES128Encryption:
    """ Clase que contiene metodos para el cifrado y descifrado de informacion con AES """

    pkcs5_padding   = lambda x, y: (x + (y - len(x) % y) * chr(y - len(x) % y)).encode("utf-8")
    pkcs5_unpadding = lambda x: x[:-ord(x[-1])]


    def AES128Encryption(self):
        #logging.info("Initializing AES-128 CBC Encryptor")
        self.BLOCK_SIZE = 128 
        

    """ Generates a String containing a concatenation of the IV and the ciphertext from encryption 
        :param plaintext 
        :param hex_key 
    """
    def encrypt(self, plaintext:str, hex_key:str) -> str:
        self.validate_enc_input(hex_key, plaintext)
        raw_key = bytes.fromhex(hex_key)
        # Random  IV 16 * 8 bits.
        iv = os.urandom(16)
        #logging.info("Generated IV: "  + iv.hex())
        # Construct an AES Cipher object with the given key and a randomly generated IV.
        cipher = Cipher(algorithms.AES(raw_key), modes.CBC(iv))
        #Initialize encryptor
        encryptor = cipher.encryptor()
        # Encrypt the plaintext and get the associated ciphertext. It must be encoded on UTF-8  charset
        ciphertext = encryptor.update(AES128Encryption.pkcs5_padding(plaintext , len(iv))) + encryptor.finalize()
        #logging.info("Generated CT: "  + ciphertext.hex())

        cryptogram = iv + ciphertext

        return base64.b64encode(cryptogram)


    """ Generates a String containing a plaintext obtained from the given ciphertext  
        :param hex_key 
        :param B64 ciphertext
    """
    def decrypt(self, hex_key:str, b64_iv_ciphertext:str) -> str:
        #self.validate_dec_input(hex_key, hex_iv_ciphertext)
        byte_iv_ciphertext = base64.b64decode(b64_iv_ciphertext)
        l = len(byte_iv_ciphertext)
        print("Len: " + str(l))
        raw_iv         = byte_iv_ciphertext[0:16] #The first 32 chars of hex string that correspond to 16 bytes
        raw_ciphertext = byte_iv_ciphertext[16:l]
        raw_key        = bytes.fromhex(hex_key)
        
        #logging.info("IV: " + raw_iv.hex())
        #logging.info("CT: " + raw_ciphertext.hex())
        # Construct a Cipher object, with the key, iv
        cipher    = Cipher( algorithms.AES(raw_key), modes.CBC(raw_iv))
        decryptor = cipher.decryptor()
        plaintext = decryptor.update(raw_ciphertext) + decryptor.finalize()
        return AES128Encryption.pkcs5_unpadding(plaintext.decode("UTF-8"))


    """ Validates encryption input
    """
    def validate_enc_input(self , key:str, plaintext:str):
        key_error = False
        if(len(key)==32):
            try:
                bytes.fromhex(key)
            except:
                key_error = True
        else:
            key_error = True
        if key_error:
            raise Exception("ENCRYPTION ERROR: Key Must be a 32 chars even len Hex String, Example: 1460C8BD91DB352E78604983F82CDA3A")
        if( len(plaintext)<1):
            raise Exception("ENCRYPTION ERROR: Plaintext Must not be Empty")



    """ Validates decryption input 
    """
    def validate_dec_input(self , key:str, ciphertext:str):
        key_error = False
        ciphertext_error = False
        if(len(key)==32):
            try:
                bytes.fromhex(key)
            except:
                key_error = True
        else:
            key_error = True
        if key_error:
            raise Exception("DECRYPTION ERROR: Key Must be a 32 chars len even Hex String, Example: 1460C8BD91DB352E78604983F82CDA3A")
        #Data representation Validation
        try:
            base64.b64decode(ciphertext)
        except:
            ciphertext_error = True
        if ciphertext_error:
            raise Exception("DECRYPTION ERROR: Ciphertext must be a base 64 String. Remember, it contains IV + ciphertext")



