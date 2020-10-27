module AesCrypto
  extend ActiveSupport::Concern

  def get_transaction_uri(plain_text, key, data0, web_pay_uri)
     key = [key].pack('H*')
     cipher_text = aes_encryption(key, plain_text)
     xml="<pgs><data0>#{data0}</data0>  <data>#{cipher_text}</data></pgs>"
     form_data = URI.encode_www_form({"xml" => xml})
     uri = URI.parse(web_pay_uri)
     https = Net::HTTP.new(uri.host, uri.port)
     https.use_ssl = true
     req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/x-www-form-urlencoded', 'cache-control' => 'no-cache'})
     req.body = form_data
     res = https.request(req)
     encrypted_string = res.body
     final_data = aes_decryption(encrypted_string, key)
     final_data.scan(/<nb_url>([^<>]*)<\/nb_url>/imu).flatten.select{|x| !x.empty?}.first
  end

  def aes_encryption(key, plain_text)
     cipher = OpenSSL::Cipher::AES.new(128, :CBC).encrypt
     cipher.key=key
     iv = cipher.random_iv
     cipher_text = cipher.update(plain_text) + cipher.final
     Base64.encode64(iv + cipher_text)
  end

  def aes_decryption(encrypted_string, key)
    decoded_string = Base64.decode64(encrypted_string)
    iv_de = decoded_string[0..15]
    data = decoded_string[16..]
    decipher = OpenSSL::Cipher::AES.new(128, :CBC).decrypt
    decipher.key = key
    decipher.iv = iv_de
    decipher.update(data) + decipher.final
  end
end
