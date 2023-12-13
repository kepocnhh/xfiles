package org.kepocnhh.xfiles.util.android

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.kepocnhh.xfiles.BuildConfig
import java.security.KeyStore
import java.security.UnrecoverableKeyException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.time.Duration

internal object BiometricUtil {
    sealed interface Broadcast {
        data class OnSucceeded(val cipher: Cipher) : Broadcast
        data class OnError(val type: Type?) : Broadcast {
            enum class Type {
                USER_CANCELED,
                CAN_NOT_AUTHENTICATE,
                UNRECOVERABLE_KEY,
            }
        }
    }

    private const val algorithm = KeyProperties.KEY_ALGORITHM_AES

//    private val blocks = KeyProperties.BLOCK_MODE_GCM
    private const val blocks = KeyProperties.BLOCK_MODE_CBC

//    private val paddings = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val paddings = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private val keyAlias = BuildConfig.APPLICATION_ID + ":biometric"
    private const val keySize = 256

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()
    private const val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
    private val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            val type = when (errorCode) {
                BiometricPrompt.ERROR_USER_CANCELED -> Broadcast.OnError.Type.USER_CANCELED
                else -> null
            }
            scope.launch {
                _broadcast.emit(Broadcast.OnError(type = type))
            }
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            val cryptoObject = result.cryptoObject ?: error("No crypto object!")
            val cipher = cryptoObject.cipher ?: error("No cipher!")
            scope.launch {
                _broadcast.emit(Broadcast.OnSucceeded(cipher = cipher))
            }
        }

        override fun onAuthenticationFailed() {
            // Called when a biometric (e.g. fingerprint, face, etc.) is presented but not recognized as belonging to the user.
        }
    }

    private fun getKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore
    }

    @Suppress("Deprecation")
    private fun KeyGenParameterSpec.Builder.setValidityDuration(duration: Duration): KeyGenParameterSpec.Builder {
        return setUserAuthenticationValidityDurationSeconds(duration.inWholeSeconds.toInt())
    }

    private fun getKeyOrCreate(): SecretKey {
        val keyStore = getKeyStore()
        if (keyStore.containsAlias(keyAlias)) return keyStore.getKey(keyAlias, null) as SecretKey
        val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        val spec = KeyGenParameterSpec
            .Builder(keyAlias, purposes)
            .setBlockModes(blocks)
            .setEncryptionPaddings(paddings)
            .setKeySize(keySize)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
            .setValidityDuration(Duration.ZERO)
            .build()
        val keyGenerator = KeyGenerator.getInstance(algorithm, keyStore.provider)
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun deleteSecretKey() {
        val keyStore = getKeyStore()
        try {
            keyStore.deleteEntry(keyAlias)
        } catch (_: Throwable) {
            // "delete entry \"$keyAlias\" error: $e"
        }
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance("$algorithm/$blocks/$paddings")
    }

    private fun getPromptInfo(
        title: CharSequence,
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
//            .setSubtitle(subtitle)
//            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .setConfirmationRequired(true)
            .build()
    }

    private fun Context.canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun getKeyOrError(context: Context): SecretKey? {
        if (!context.canAuthenticate()) {
            scope.launch {
                _broadcast.emit(Broadcast.OnError(Broadcast.OnError.Type.CAN_NOT_AUTHENTICATE))
            }
            return null
        }
        val key = try {
            getKeyOrCreate()
        } catch (_: UnrecoverableKeyException) {
            scope.launch {
                _broadcast.emit(Broadcast.OnError(Broadcast.OnError.Type.UNRECOVERABLE_KEY))
            }
            return null
        }
        return key
    }

    fun authenticate(
        activity: FragmentActivity,
        title: CharSequence,
    ) {
        val key = getKeyOrError(activity) ?: return
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val info = getPromptInfo(title = title)
        BiometricPrompt(activity, callback).authenticate(info, BiometricPrompt.CryptoObject(cipher))
    }

    fun authenticate(
        activity: FragmentActivity,
        title: CharSequence,
        iv: ByteArray,
    ) {
        val key = getKeyOrError(activity) ?: return
        val cipher = getCipher()
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        val info = getPromptInfo(title = title)
        BiometricPrompt(activity, callback).authenticate(info, BiometricPrompt.CryptoObject(cipher))
    }
}
