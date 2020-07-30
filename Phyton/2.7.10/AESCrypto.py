from base64 import b64decode
from base64 import b64encode
from Crypto import Random
from Crypto.Cipher import AES

block_size = AES.block_size


class AESCrypto:
	def __init__(self, key):
		self.key = key
		self.bs = 16

	def _unpadPKCS5(self, s):
		return s[0:-ord(s[-1])]

	def _padPKCS5(self, s):
		return s + (
		    self.bs - len(s) % self.bs) * chr(self.bs - len(s) % self.bs)

	def encrypt(self, raw):
		padded_plain_text = self._padPKCS5(raw)
		iv = Random.new().read(AES.block_size)
		cipher = AES.new(self.key, AES.MODE_CBC, iv)
		ivEncoded = b64encode(iv)
		dataEncoded = b64encode(cipher.encrypt(padded_plain_text))
		return b64encode(dataEncoded+':'+ivEncoded)
		

	def decrypt(self, enc):
		enc = b64decode(enc)
		dataArr = enc.split(":")
		iv = b64decode(dataArr[1])
		enc = b64decode(dataArr[0])
		cipher = AES.new(self.key, AES.MODE_CBC, iv)
		return self._unpadPKCS5(cipher.decrypt(enc)).decode('utf-8')
		
key = "0123456789abcdef" or b"0123456789abcdef"
plaintext = "pruebadesdepython2" or b"pruebadesdepython2"
ciphertext = AESCipher(key).encrypt(plaintext)
print(ciphertext)
textfromjava = "NXZkVks4MVV6TlgzY2hBZTRsTFd2Zz09OnZxb2xkS05zS0lkWVVwV1o5S05rckE9PQ==" or b"NXZkVks4MVV6TlgzY2hBZTRsTFd2Zz09OnZxb2xkS05zS0lkWVVwV1o5S05rckE9PQ=="
cleartext = AESCipher(key).decrypt(textfromjava)
print(cleartext)