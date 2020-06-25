Imports System.IO
Imports System.Security.Cryptography


''
'' @author MIT
'' @version 1.0
'' @date 2020/06/25
''


Public NotInheritable Class AESCrypto

    Public Shared Function Encrypt(ByVal strToEncrypt As String, ByVal strKey As String) As String

        Try
            Dim original As String = strToEncrypt.Replace(vbCrLf, "")

            Using myAes As Aes = Aes.Create()
                myAes.Mode = CipherMode.CBC
                myAes.KeySize = 128
                myAes.Padding = PaddingMode.PKCS7
                myAes.BlockSize = 128
                myAes.FeedbackSize = 128
                Dim key As Byte() = New Byte() {}
                Dim str As String = strKey
                key = StringToByteArray(str)
                myAes.Key = key
                Dim encrypted As Byte() = EncryptStringToBytes_Aes128(original, myAes.Key, myAes.IV)
                Dim resultado As Byte() = New Byte(encrypted.Length + myAes.IV.Length - 1) {}
                Array.Copy(myAes.IV, 0, resultado, 0, myAes.IV.Length)
                Array.Copy(encrypted, 0, resultado, myAes.IV.Length, encrypted.Length)
                Dim textBase64 As String = System.Convert.ToBase64String(resultado)
                Return textBase64

            End Using

        Catch e As Exception
            Console.WriteLine("Error: {0}", e.Message)
            Return ""
        End Try

    End Function

    Private Shared Function StringToByteArray(ByVal hex As String) As Byte()
        Return Enumerable.Range(0, hex.Length).Where(Function(x) x Mod 2 = 0).[Select](Function(x) Convert.ToByte(hex.Substring(x, 2), 16)).ToArray()
    End Function

    Private Shared Function EncryptStringToBytes_Aes128(ByVal plainText As String, ByVal Key As Byte(), ByVal IV As Byte()) As Byte()
        If plainText Is Nothing OrElse plainText.Length <= 0 Then Throw New ArgumentNullException("plainText")
        If Key Is Nothing OrElse Key.Length <= 0 Then Throw New ArgumentNullException("Key")
        If IV Is Nothing OrElse IV.Length <= 0 Then Throw New ArgumentNullException("Key")
        Dim encrypted As Byte()

        Using aesAlg As Aes = Aes.Create()
            aesAlg.Mode = CipherMode.CBC
            aesAlg.KeySize = 128
            aesAlg.Padding = PaddingMode.PKCS7
            aesAlg.BlockSize = 128
            aesAlg.FeedbackSize = 128
            aesAlg.Key = Key
            aesAlg.IV = IV
            Dim encryptor As ICryptoTransform = aesAlg.CreateEncryptor(aesAlg.Key, aesAlg.IV)

            Using msEncrypt As MemoryStream = New MemoryStream()

                Using csEncrypt As CryptoStream = New CryptoStream(msEncrypt, encryptor, CryptoStreamMode.Write)

                    Using swEncrypt As StreamWriter = New StreamWriter(csEncrypt)
                        swEncrypt.Write(plainText)
                    End Using

                    encrypted = msEncrypt.ToArray()
                End Using
            End Using
        End Using

        Return encrypted
    End Function


    Public Shared Function Decrypt(ByVal strKey As String, ByVal strText As String) As String
        Try
            Dim original As String = strText

            Using myAes As Aes = Aes.Create()
                myAes.Mode = CipherMode.CBC
                myAes.KeySize = 128
                myAes.Padding = PaddingMode.PKCS7
                myAes.BlockSize = 128
                myAes.FeedbackSize = 128
                Dim key As Byte() = New Byte() {}
                Dim xmlByte As Byte() = New Byte() {}
                Dim result As String = ""
                Dim str As String = strKey
                key = StringToByteArray(str)
                Dim base64EncodedBytes = System.Convert.FromBase64String(strText)
                Dim IVAES128 As Byte() = New Byte(15) {}
                Array.Copy(base64EncodedBytes, 0, IVAES128, 0, 16)
                myAes.IV = IVAES128
                base64EncodedBytes = System.Convert.FromBase64String(strText)
                xmlByte = New Byte(base64EncodedBytes.Length - 16 - 1) {}
                Array.Copy(base64EncodedBytes, 16, xmlByte, 0, base64EncodedBytes.Length - 16)
                myAes.Key = key
                result = DecryptStringFromBytes_Aes128(xmlByte, myAes.Key, myAes.IV)
                Return result
            End Using

        Catch e As Exception
            Console.WriteLine("Error: {0}", e.Message)
            Return ""
        End Try
    End Function

    Private Shared Function DecryptStringFromBytes_Aes128(ByVal cipherText As Byte(), ByVal Key As Byte(), ByVal IV As Byte()) As String
        If cipherText Is Nothing OrElse cipherText.Length <= 0 Then Throw New ArgumentNullException("cipherText")
        If Key Is Nothing OrElse Key.Length <= 0 Then Throw New ArgumentNullException("Key")
        If IV Is Nothing OrElse IV.Length <= 0 Then Throw New ArgumentNullException("Key")
        Dim plaintext As String = Nothing

        Using aesAlg As Aes = Aes.Create()
            aesAlg.Key = Key
            aesAlg.IV = IV
            aesAlg.Mode = CipherMode.CBC
            aesAlg.KeySize = 128
            aesAlg.Padding = PaddingMode.PKCS7
            aesAlg.BlockSize = 128
            aesAlg.FeedbackSize = 128
            aesAlg.Key = Key
            aesAlg.IV = IV
            Dim decryptor As ICryptoTransform = aesAlg.CreateDecryptor(aesAlg.Key, aesAlg.IV)

            Using msDecrypt As MemoryStream = New MemoryStream(cipherText)

                Using csDecrypt As CryptoStream = New CryptoStream(msDecrypt, decryptor, CryptoStreamMode.Read)

                    Using srDecrypt As StreamReader = New StreamReader(csDecrypt)
                        plaintext = srDecrypt.ReadToEnd()
                    End Using
                End Using
            End Using
        End Using

        Return plaintext
    End Function

End Class
