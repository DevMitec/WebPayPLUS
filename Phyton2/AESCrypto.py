from base64 import b64decode
from base64 import b64encode
from Crypto import Random
from Crypto.Cipher import AES

block_size = AES.block_size


class AESCipher:
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
		return b64encode(iv + cipher.encrypt(padded_plain_text))

	def decrypt(self, enc):
		enc = b64decode(enc)
		iv = enc[:16]
		cipher = AES.new(self.key, AES.MODE_CBC, iv)
		return self._unpadPKCS5(cipher.decrypt(enc[16:])).decode('utf-8')
