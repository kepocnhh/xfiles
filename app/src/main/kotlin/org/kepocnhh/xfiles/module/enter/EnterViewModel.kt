package org.kepocnhh.xfiles.module.enter

import android.util.Base64
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.entity.KeyMeta
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.provider.readBytes
import org.kepocnhh.xfiles.provider.readText
import org.kepocnhh.xfiles.util.base64
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import org.kepocnhh.xfiles.util.security.decrypt
import org.kepocnhh.xfiles.util.security.encrypt
import org.kepocnhh.xfiles.util.security.generateKeyPair
import org.kepocnhh.xfiles.util.security.getCipherAlgorithm
import org.kepocnhh.xfiles.util.security.getSecureRandom
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.Security
import java.security.Signature
import java.security.interfaces.DSAParams
import java.security.interfaces.DSAPrivateKey
import java.security.spec.DSAParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

internal class EnterViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnUnlock(val key: SecretKey) : Broadcast
        object OnUnlockError : Broadcast
    }

    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private val _exists = MutableStateFlow<Boolean?>(null)
    val exists = _exists.asStateFlow()

    fun requestFile() {
        injection.launch {
            _exists.value = withContext(injection.contexts.default) {
                injection.files.exists("db.json.enc")
            }
        }
    }

    private fun KeyMeta.toJson(): JSONObject {
        return JSONObject()
            .put("algorithm", algorithm)
            .put("salt", salt.base64())
            .put("iv", iv.base64())
            .put("iterations", iterations)
            .put("bits", bits)
    }

    private fun hash(pin: String): ByteArray {
        val md = MessageDigest.getInstance("SHA-512")
        return md.digest(pin.toByteArray())
    }

    private fun getP(
        q: BigInteger,
        L: Int,
        certainty: Int,
        random: SecureRandom,
    ): BigInteger {
        check(q.isProbablePrime(certainty))
        // (p - 1) % q == 0
        val m = BigInteger.ZERO
        var index = 0
        while (true) {
            index++
//            if (index > 10) TODO()
            println("$index]")
            // a / b = c
            // a % b = m
            // b * c + m = a
            // (p - 1) / q = c
            // (p - 1) % q = m
            // p - 1 = c * q + m
            // q * c + m = (p - 1)
            // q * c + m + 1 = p
            // m = 0 // q * c + 1 = p
//            val c = BigInteger(L, certainty, random)
            // p = q * c + m + 1
            // q * c = p - m - 1
            // c = (p - m - 1) / q
//            val cLen = ((BigInteger(L, certainty, random) - BigInteger.ONE - m) / q).bitLength()
//            val c = BigInteger(cLen, certainty, random)
//            println("\ttmp(c): bit length: ${c.bitLength()}")
//            val p = q * c + m + BigInteger.ONE
//            val w = ByteArray(L / 8)
//            val p = BigInteger(1, w)
//            val l1 = BigInteger(L, certainty, random)
//            val l2 = l1.subtract(BigInteger.ONE) // l2 = l1 - 1
//            val p = l1.subtract(l2.remainder(q)) // p = l1 - (l2 % q) // p = l1 - ((l1 - 1) % q)
//            val w = ByteArray(L / 8)
//            val x = BigInteger(1, w)
//            val c = x.mod(q.shiftLeft(1))
//            val p = x.subtract(c.subtract(BigInteger.ONE))
            val l1 = BigInteger(L, certainty, random)
//            val lm = l1.subtract(BigInteger.ONE).mod(q) // lm = (l1 - 1) % q
            val ld = l1.subtract(BigInteger.ONE).div(q) // ld = (l1 - 1) / q
            // ld * q = l1 - 1
            // ld * q + 1 = p
//            val p = ld * l1.subtract(BigInteger.ONE) // p = ld * (l1 - 1)
            val p = ld.multiply(q).add(BigInteger.ONE) // p = ld * q + 1
//            val p = BigInteger(L, certainty, random)
            println("\ttmp(p): $p")
            println("\ttmp(p): bit length: ${p.bitLength()}")
            if (p.bitLength() != L) continue
            println("\tp-1: ${p.subtract(BigInteger.ONE)}")
            println("\t(p-1)%q: ${p.subtract(BigInteger.ONE).mod(q)}")
            if (p.subtract(BigInteger.ONE).mod(q) != m) continue
            if (p.isProbablePrime(certainty)) return p
//            if (p.subtract(BigInteger.ONE).mod(q) == BigInteger.ZERO) {
//                return p
//            }
        }
    }

    private fun getG(
        p: BigInteger,
        q: BigInteger,
        random: SecureRandom,
    ): BigInteger {
        // g = h^((p-1)/q) mod p
        val p1 = p.subtract(BigInteger.ONE) // p - 1
        println("\t(p - 1): $p1")
        val e = p1.divide(q) // (p - 1) / q
        println("\t((p - 1) / q): $e")
        while (true) {
            // h in (1, (p-1))
            val h = BigInteger(p1.bitLength(), random)
            println("\ttmp(h): $h")
            if (h in BigInteger.ONE..p1) continue
            val g = h.modPow(e, p)
            println("\ttmp(g): $g")
            if (g == BigInteger.ONE) continue
            return g
        }
    }

    private fun getSpec(
        L: Int,
        N: Int,
        certainty: Int,
        random: SecureRandom,
    ): DSAParameterSpec {
        // https://www.rfc-editor.org/rfc/rfc6979
        check(setOf(1024, 2048, 3072).contains(L))
        when (L) {
            1024 -> check(N == 160)
            2048 -> check(N == 224 || N == 256)
            3072 -> check(N == 256)
        }
        println("L: $L")
        println("N: $N")
        val q: BigInteger
        while (true) {
            val tmp = BigInteger(N, certainty, random)
            if (tmp.isProbablePrime(certainty)) {
                q = tmp
                break
            }
        }
        println("q: $q")
        val p = getP(q = q, L = L, certainty = certainty, random)
        println("p: $p")
        return DSAParameterSpec(p, q, getG(p = p, q = q, random))
    }

    private fun create(pin: String): SecretKey {
        val startTime = System.currentTimeMillis().milliseconds // todo
        val hash = hash(pin = pin).base64()
        val random = getSecureRandom()
//        val bits = 8 * 8 // 64
//        val bits = 8 * 16 // 128
        val bits = 8 * 32 // 256 bits (32 octets)
        val blockSize = 16 // AES-256
        val meta = KeyMeta(
            algorithm = getCipherAlgorithm(),
            salt = ByteArray(bits / 8).also(random::nextBytes),
            iv = ByteArray(blockSize).also(random::nextBytes),
//            iterations = 2.0.pow(10).toInt(),
            iterations = 2.0.pow(16).toInt(),
//            iterations = 2.0.pow(20).toInt(),
//            iterations = 1_048_576,
            bits = bits,
        )
        println("create meta: ${System.currentTimeMillis().milliseconds - startTime}")
        injection.files.writeBytes("sym.json", meta.toJson().toString().toByteArray())
//        val primes = Cipher.getMaxAllowedKeyLength("DSA")
        val pair = KeyPairGenerator.getInstance("DSA").let { generator ->
//            generator.initialize(1024, random)
//            L = 1024, N = 160
//            L = 2048, N = 224
//            L = 2048, N = 256
//            L = 3072, N = 256
//            strength must be from 512 - 4096 and a multiple of 1024 above 1024
            val primes = 1024 * 1
//            val primes = 1024 * 2
            generator.initialize(primes, random)
            val subPrimes = 160 // must be 160 for primes = 1024
//            val subPrimes = 256 // must be 224 or 256 for primes = 2048
            val certainty = 20
//            val certainty = 100
//            generator.initialize(getSpec(L = primes, N = subPrimes, certainty = certainty, random))
            generator.generateKeyPair().also {
                val private = it.private
                check(private is DSAPrivateKey)
                println(
                    """
                        prime: [${private.params.p.bitLength()}] ${private.params.p}
                        subPrime: [${private.params.q.bitLength()}] ${private.params.q}
                        base: [${private.params.g.bitLength()}] ${private.params.g}
                    """.trimIndent()
                )
            }
        }
        println("generate key pair: ${System.currentTimeMillis().milliseconds - startTime}")
        val decrypted = "{}".toByteArray()
        val cipher = Cipher.getInstance(meta.algorithm)
        val key = SecretKeyFactory.getInstance(cipher.algorithm).let { factory ->
            val spec = PBEKeySpec(hash.toCharArray(), meta.salt, meta.iterations, meta.bits)
            factory.generateSecret(spec)
        }
        println("generate secret key: ${System.currentTimeMillis().milliseconds - startTime}")
        val params = IvParameterSpec(meta.iv)
        injection.files.writeBytes("db.json.enc", cipher.encrypt(key, params, decrypted))
        val private = cipher.encrypt(key, params, pair.private.encoded)
        println("encrypt: ${System.currentTimeMillis().milliseconds - startTime}")
        JSONObject()
            .put("public", pair.public.encoded.base64())
            .put("private", private.base64())
            .also { json ->
                injection.files.writeBytes("asym.json", json.toString().toByteArray())
            }
        Signature.getInstance("SHA256WithDSA").also { signature ->
            signature.initSign(pair.private, random)
            println("init sign: ${System.currentTimeMillis().milliseconds - startTime}")
            signature.update(decrypted)
            println("signature update: ${System.currentTimeMillis().milliseconds - startTime}")
            injection.files.writeBytes("db.json.sig", signature.sign())
            println("signature sign: ${System.currentTimeMillis().milliseconds - startTime}")
        }
        return key
    }

    fun createNewFile(pin: String) {
        injection.launch {
            _exists.value = null
            val key = withContext(injection.contexts.default) {
                create(pin)
            }
            _broadcast.emit(Broadcast.OnUnlock(key))
        }
    }

    fun deleteFile() {
        injection.launch {
            _exists.value = null
            withContext(injection.contexts.default) {
                injection.files.delete("db.json.enc")
            }
            _exists.value = false
        }
    }

    private fun JSONObject.toKeyMeta(): KeyMeta {
        return KeyMeta(
            algorithm = getString("algorithm"),
            salt = getString("salt").let { Base64.decode(it, Base64.DEFAULT) },
            iv = getString("iv").let { Base64.decode(it, Base64.DEFAULT) },
            iterations = getInt("iterations"),
            bits = getInt("bits"),
        )
    }

    private fun unlock(pin: String): SecretKey {
        val startTime = System.currentTimeMillis().milliseconds // todo
        val chars = hash(pin = pin).base64().toCharArray()
        val meta = JSONObject(injection.files.readText("sym.json")).toKeyMeta()
        val cipher = Cipher.getInstance(meta.algorithm)
        val key = SecretKeyFactory.getInstance(cipher.algorithm).let { factory ->
            val spec = PBEKeySpec(chars, meta.salt, meta.iterations, meta.bits)
            factory.generateSecret(spec)
        }
        println("generate secret key: ${System.currentTimeMillis().milliseconds - startTime}")
        val params = IvParameterSpec(meta.iv)
        val pair = JSONObject(injection.files.readText("asym.json")).let { json ->
            val public = json.getString("public").let { Base64.decode(it, Base64.DEFAULT) }
            val encrypted = json.getString("private").let { Base64.decode(it, Base64.DEFAULT) }
            KeyFactory.getInstance("DSA").generateKeyPair(
                public = public,
                private = cipher.decrypt(key, params, encrypted),
            )
        }
        println("generate key pair: ${System.currentTimeMillis().milliseconds - startTime}")
        val decrypted = cipher.decrypt(key, params, injection.files.readBytes("db.json.enc"))
        println("decrypt: ${System.currentTimeMillis().milliseconds - startTime}")
        Signature.getInstance("SHA256WithDSA").also { signature ->
            signature.initVerify(pair.public)
            println("init verify: ${System.currentTimeMillis().milliseconds - startTime}")
            signature.update(decrypted)
            println("signature update: ${System.currentTimeMillis().milliseconds - startTime}")
            val verified = signature.verify(injection.files.readBytes("db.json.sig"))
            println("signature verify: ${System.currentTimeMillis().milliseconds - startTime}")
            check(verified)
        }
        return key
    }

    fun unlockFile(pin: String) {
        injection.launch {
            _exists.value = null
            val result = withContext(injection.contexts.default) {
                runCatching {
                    unlock(pin = pin)
                }
            }
            result.fold(
                onFailure = {
                    _exists.value = true
                    _broadcast.emit(Broadcast.OnUnlockError)
                },
                onSuccess = {
                    _broadcast.emit(Broadcast.OnUnlock(it))
                }
            )
        }
    }
}
