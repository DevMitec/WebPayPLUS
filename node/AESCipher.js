const crypto = require('crypto')

module.exports = {
  encrypt: (payload, key) => {
    const iv = crypto.randomBytes(16)
    const aesCipher = crypto.createCipheriv('aes-128-cbc', Buffer.from(key, 'hex'), iv)
    let encrypted = iv
    encrypted = Buffer.concat([encrypted, aesCipher.update(payload, 'utf8')])
    encrypted = Buffer.concat([encrypted, aesCipher.final()])
    return encrypted.toString('base64')
  },
  decrypt: (payload, key) => {
    const buffer = Buffer.from(payload,'base64')
    const iv = buffer.slice(0,16)
    const encryptedData = buffer.slice(16,buffer.length)
    const aesDecipher = crypto.createDecipheriv('aes-128-cbc', Buffer.from(key, 'hex'), iv)
    aesDecipher.setAutoPadding = true
    let decrypted = aesDecipher.update(encryptedData,'base64')
    decrypted = Buffer.concat([decrypted,aesDecipher.final()])
    return decrypted.toString('utf8')
  }
}
