package encrypt
// from: https://www.programmersought.com/article/6109709437/


import (
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/base64"
    "encoding/hex"
	"io"
)

/*CBC encryption Follow the example code of the golang standard library
But there is no padding inside, so make up
*/

// Use PKCS7 to fill, IOS is also 7
func PKCS7Padding(ciphertext []byte, blockSize int) []byte {
   padding := blockSize - len(ciphertext) % blockSize
   padtext := bytes.Repeat([]byte{byte(padding)}, padding)
   return append(ciphertext, padtext...)
}
 
func PKCS7UnPadding(origData []byte) []byte {
   length := len(origData)
   unpadding := int(origData[length-1])
   return origData[:(length - unpadding)]
}
 
 //aes encryption, filling the 16 bits of the key key, 24, 32 respectively corresponding to AES-128, AES-192, or AES-256.
func AesCBCEncrypt(rawData, key []byte) ([]byte, error) {
   block, err := aes.NewCipher(key)
   if err != nil {
       panic(err)
   }
       //fill the original
   blockSize := block.BlockSize()
   rawData = PKCS7Padding(rawData, blockSize)
       // Initial vector IV must be unique, but does not need to be kept secret
   cipherText := make([]byte,blockSize+len(rawData))
       //block size 16
   iv := cipherText[:blockSize]
   if _, err := io.ReadFull(rand.Reader,iv); err != nil {
       panic(err)
   }
 
    //block size and initial vector size must be the same
   mode := cipher.NewCBCEncrypter(block,iv)
   mode.CryptBlocks(cipherText[blockSize:],rawData)

   final := append(iv[:], cipherText[:]...)
   // finalStr := base64.StdEncoding.EncodeToString(final)
   return final, nil
}
 
func AesCBCDncrypt(encryptData, key []byte) ([]byte,error) {
   block, err := aes.NewCipher(key)
   if err != nil {
       panic(err)
   }
 
   blockSize := block.BlockSize()
 
   if len(encryptData) < blockSize {
       panic("ciphertext too short")
   }
   iv := encryptData[:blockSize]
   encryptData = encryptData[blockSize:]
 
   // CBC mode always works in whole blocks.
   if len(encryptData)%blockSize != 0 {
       panic("ciphertext is not a multiple of the block size")
   }
 
   mode := cipher.NewCBCDecrypter(block, iv)
 
   // CryptBlocks can work in-place if the two arguments are the same.
   mode.CryptBlocks(encryptData, encryptData)
    // Unfill
   encryptData = PKCS7UnPadding(encryptData)
   return encryptData,nil
}
 
 
func Encrypt(plaintext string, secret string) (string,error) {
    key, _ := hex.DecodeString(secret)
	rawData := []byte(plaintext)
   data, err:= AesCBCEncrypt(rawData,key)
   if err != nil {
       return "",err
   }
   return base64.StdEncoding.EncodeToString(data),nil
}
 
func Dncrypt(plaintext string,secret string) (string,error) {
    key, _ := hex.DecodeString(secret)
	// rawData := []byte(plaintext)
   data,err := base64.StdEncoding.DecodeString(plaintext)
   if err != nil {
       return "",err
   }
   dnData,err := AesCBCDncrypt(data,key)
   if err != nil {
       return "",err
   }
   return string(dnData),nil
}