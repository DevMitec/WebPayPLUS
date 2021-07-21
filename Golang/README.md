# Golang implementation:

Base code from: [https://www.programmersought.com/article/6109709437/](https://www.programmersought.com/article/6109709437/)


```golang
import "example.com/encrypt"

func main() {
    originalString := "[USAR LA CADENA DEL EJEMPLO UNO]"
    key := "5DCC67393750523CD165F17E1EFADD21"

    encrypt.Encrypt(originalString, key)
    .
    .
    .
    originalString := "Este es el texto a procesar"
    key := "5DCC67393750523CD165F17E1EFADD21"
    respDecryted, err := encrypt.Dncrypt(originalString, key)
}
```

