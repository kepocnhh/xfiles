package org.kepocnhh.xfiles.util.security

import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey

internal fun Cipher.encrypt(key: PublicKey, decrypted: ByteArray): ByteArray {
    init(Cipher.ENCRYPT_MODE, key)
    return doFinal(decrypted)
}

internal fun Cipher.encrypt(key: SecretKey, params: AlgorithmParameterSpec, decrypted: ByteArray): ByteArray {
    init(Cipher.ENCRYPT_MODE, key, params)
    return doFinal(decrypted)
}

internal fun Cipher.decrypt(key: SecretKey, params: AlgorithmParameterSpec, decrypted: ByteArray): ByteArray {
    init(Cipher.DECRYPT_MODE, key, params)
    return doFinal(decrypted)
}

internal fun Cipher.decrypt(key: PrivateKey, decrypted: ByteArray): ByteArray {
    init(Cipher.DECRYPT_MODE, key)
    return doFinal(decrypted)
}

internal fun getCipherAlgorithm(): String {
    // DK: derived key
    // KDF: key derivation function
    // DK = KDF(P, S): P is the password, and S is the salt
    // IV: initialization vector
    // EM: encoded message
    // PBE: password-based encryption
    // PS: padding string
    // CBC: cipher-block chaining
    // PBKDF2: password-based key derivation function RFC 2898 https://datatracker.ietf.org/doc/html/rfc2898
    // DK = PBKDF2(P, S, c, dkLen)
    //     P - password, an octet string, length is hLen
    //     S - salt, an octet string
    //     c - iteration count, a positive integer
    //     dkLen - intended length in octets of the DK, a positive integer, at most (2^32 - 1) * hLen
    //
    // --------------------------------+
    // bytes   | Key size | Block size |
    // --------+----------+------------|
    // AES-128 | 16       | 16         |
    // AES-192 | 24       | 16         |
    // AES-256 | 32       | 16         |
    // --------------------------------+
    //
//    println("algorithms: " + Security.getAlgorithms("SecretKeyFactory"))
    /*
    AES
    DES
    DESEDE
    HMACSHA1
    HMACSHA224
    HMACSHA256
    HMACSHA384
    HMACSHA512
    PBEWITHHMACSHA1
    PBEWITHHMACSHA1ANDAES_128
    PBEWITHHMACSHA1ANDAES_256
    PBEWITHHMACSHA224ANDAES_128
    PBEWITHHMACSHA224ANDAES_256
    PBEWITHHMACSHA256ANDAES_128
    PBEWITHHMACSHA256ANDAES_256
    PBEWITHHMACSHA384ANDAES_128
    PBEWITHHMACSHA384ANDAES_256
    PBEWITHHMACSHA512ANDAES_128
    PBEWITHHMACSHA512ANDAES_256
    PBEWITHMD5AND128BITAES-CBC-OPENSSL
    PBEWITHMD5AND192BITAES-CBC-OPENSSL
    PBEWITHMD5AND256BITAES-CBC-OPENSSL
    PBEWITHMD5ANDDES
    PBEWITHMD5ANDRC2
    PBEWITHSHA1ANDDES
    PBEWITHSHA1ANDRC2
    PBEWITHSHA256AND128BITAES-CBC-BC
    PBEWITHSHA256AND192BITAES-CBC-BC
    PBEWITHSHA256AND256BITAES-CBC-BC
    PBEWITHSHAAND128BITAES-CBC-BC
    PBEWITHSHAAND128BITRC2-CBC
    PBEWITHSHAAND128BITRC4
    PBEWITHSHAAND192BITAES-CBC-BC
    PBEWITHSHAAND2-KEYTRIPLEDES-CBC
    PBEWITHSHAAND256BITAES-CBC-BC
    PBEWITHSHAAND3-KEYTRIPLEDES-CBC
    PBEWITHSHAAND40BITRC2-CBC
    PBEWITHSHAAND40BITRC4
    PBEWITHSHAANDTWOFISH-CBC
    PBKDF2WITHHMACSHA1
    PBKDF2WITHHMACSHA1AND8BIT
    PBKDF2WITHHMACSHA224
    PBKDF2WITHHMACSHA256
    PBKDF2WITHHMACSHA384
    PBKDF2WITHHMACSHA512
    */
    //
    val serviceName = "Cipher"
    val algorithms = Security.getAlgorithms(serviceName)
    /*
    AES
    AES/CBC/NOPADDING
    AES/CBC/PKCS5PADDING
    AES/CBC/PKCS7PADDING
    AES/CTR/NOPADDING
    AES/ECB/NOPADDING
    AES/ECB/PKCS5PADDING
    AES/ECB/PKCS7PADDING
    AES/GCM/NOPADDING
    AESWRAP
    ARC4
    BLOWFISH
    DES
    DESEDE
    DESEDE/CBC/NOPADDING
    DESEDE/CBC/PKCS5PADDING
    DESEDEWRAP
    PBEWITHMD5AND128BITAES-CBC-OPENSSL
    PBEWITHMD5AND192BITAES-CBC-OPENSSL
    PBEWITHMD5AND256BITAES-CBC-OPENSSL
    PBEWITHMD5ANDDES
    PBEWITHMD5ANDRC2
    PBEWITHSHA1ANDDES
    PBEWITHSHA1ANDRC2
    PBEWITHSHA256AND128BITAES-CBC-BC
    PBEWITHSHA256AND192BITAES-CBC-BC
    PBEWITHSHA256AND256BITAES-CBC-BC
    PBEWITHSHAAND128BITAES-CBC-BC
    PBEWITHSHAAND128BITRC2-CBC
    PBEWITHSHAAND128BITRC4
    PBEWITHSHAAND192BITAES-CBC-BC
    PBEWITHSHAAND2-KEYTRIPLEDES-CBC
    PBEWITHSHAAND256BITAES-CBC-BC
    PBEWITHSHAAND3-KEYTRIPLEDES-CBC
    PBEWITHSHAAND40BITRC2-CBC
    PBEWITHSHAAND40BITRC4
    PBEWITHSHAANDTWOFISH-CBC
    RSA
    RSA/ECB/NOPADDING
    RSA/ECB/OAEPPADDING
    RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING
    RSA/ECB/OAEPWITHSHA-224ANDMGF1PADDING
    RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING
    RSA/ECB/OAEPWITHSHA-384ANDMGF1PADDING
    RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING
    RSA/ECB/PKCS1PADDING
    */
    val expected = setOf(
        "PBEWITHHMACSHA256ANDAES_256",
        "PBEWITHSHA256AND256BITAES-CBC-BC",
    )
    return expected.firstOrNull(algorithms::contains) ?: throw NoSuchAlgorithmException("No such algorithm \"$serviceName\"!")
}
