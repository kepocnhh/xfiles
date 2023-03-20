package org.kepocnhh.xfiles

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Security
import java.security.Signature
import java.util.Date
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class MainActivity : AppCompatActivity() {
    companion object {
        private const val provider = "AndroidKeyStore"
        private const val alias = "foo"
        private const val digest = KeyProperties.DIGEST_SHA256
        private const val algorithm = KeyProperties.KEY_ALGORITHM_RSA
        private const val blockMode = KeyProperties.BLOCK_MODE_ECB
        private const val encryptionPadding = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1
        private val md = MessageDigest.getInstance(digest)
    }

    private fun canAuthenticate() {
        val biometricManager = BiometricManager.from(this)
        val message = setOf(
            Authenticators.BIOMETRIC_WEAK or Authenticators.DEVICE_CREDENTIAL to "WEAK or DC",
            Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL to "STRONG or DC"
        ).map { (authenticator, name) ->
            val result = when (biometricManager.canAuthenticate(authenticator)) {
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    "ERROR_HW_UNAVAILABLE"
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    "ERROR_NONE_ENROLLED"
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    "ERROR_NO_HARDWARE"
                }
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                    "ERROR_SECURITY_UPDATE_REQUIRED"
                }
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                    "ERROR_UNSUPPORTED"
                }
                BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                    "STATUS_UNKNOWN"
                }
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    "SUCCESS"
                }
                else -> {
                    "UNKNOWN"
                }
            }
            name to result
        }.joinToString(separator = "\n") { (name, result) -> "$name: $result" }
        showToast(message)
    }

    private fun getSecureRandom(): SecureRandom {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SecureRandom.getInstanceStrong()
        } else {
            val algorithm = "SHA1PRNG"
            SecureRandom.getInstance(algorithm)
        }
    }

    private fun deleteKey() {
        val keyStore = KeyStore.getInstance(provider).also { it.load(null) }
        if (keyStore.aliases().toList().contains(alias)) {
            keyStore.deleteEntry(alias)
        }
        showToast("aliases: " + keyStore.aliases().toList())
    }

    private fun genKeyPair() {
        val keyStore = KeyStore.getInstance(provider).also { it.load(null) }
        if (keyStore.aliases().toList().contains(alias)) {
            showToast("aliases: " + keyStore.aliases().toList())
            return
        }
        val keyPairGenerator = KeyPairGenerator.getInstance(algorithm, provider)
        val purposes = KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_DECRYPT
        val notBefore = System.currentTimeMillis().milliseconds
        val notAfter = notBefore + 365.days
        val random = getSecureRandom()
        val signaturePaddings = KeyProperties.SIGNATURE_PADDING_RSA_PKCS1
        val spec = KeyGenParameterSpec.Builder(alias, purposes)
            .setCertificateSubject(X500Principal("CN=$alias"))
            .setCertificateNotBefore(Date(notBefore.inWholeMilliseconds))
            .setCertificateNotAfter(Date(notAfter.inWholeMilliseconds))
            .setCertificateSerialNumber(BigInteger(64, random))
            .setKeyValidityStart(Date(notBefore.inWholeMilliseconds))
            .setKeyValidityEnd(Date(notAfter.inWholeMilliseconds))
            .setBlockModes(blockMode)
            .setEncryptionPaddings(encryptionPadding)
            .setDigests(digest)
            .setSignaturePaddings(signaturePaddings)
            .setKeySize(2048)
            .setUserAuthenticationRequired(true)
            .also { builder ->
                val duration = 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val type =
                        KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
                    builder.setUserAuthenticationParameters(duration, type)
                } else {
                    builder.setUserAuthenticationValidityDurationSeconds(duration)
                }
            }
            .build()
        keyPairGenerator.initialize(spec)
        val keyPair = keyPairGenerator.genKeyPair()
        val encoded: ByteArray? = keyPair.private.encoded
        if (encoded == null) {
            // todo
        } else {
            val message = "SHA-256: " + BigInteger(md.digest(encoded)).toString(16)
            showToast(message)
        }
    }

    private fun getAliases() {
        val keyStore = KeyStore.getInstance(provider).also { it.load(null) }
        showToast("aliases: " + keyStore.aliases().toList())
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                getPrivateKey()
            }
            else -> {
                showToast("no ok")
            }
        }
    }

    private fun getPrivateKey() {
        val keyStore = KeyStore.getInstance(provider).also { it.load(null) }
        val privateKey = keyStore.getKey(alias, null)
        if (privateKey == null) {
            showToast("Key by \"$alias\" does not exist.")
            return
        }
        check(privateKey is PrivateKey)
        val decoded = "bar"
        try {
            val signature = Signature.getInstance("SHA256withRSA").let {
                it.initSign(privateKey)
                it.update(decoded.toByteArray())
                it.sign()
            }
            val message = "signature of \"$decoded\": " + BigInteger(md.digest(signature)).toString(16)
            showToast(message)
        } catch (e: UserNotAuthenticatedException) {
            authenticate(
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        showToast("Authentication error: $errorCode $errString")
                    }

                    override fun onAuthenticationFailed() {
                        showToast("Authentication failed!")
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        getPrivateKey()
                    }
                }
            )
//            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//            val intent = keyguardManager.createConfirmDeviceCredentialIntent(
//                "...keyguard manager title",
//                "...keyguard manager description"
//            )
//            launcher.launch(intent)
        } catch (e: Throwable) {
            showToast("Unknown error: $e")
        }
        return // todo
        val encoded: ByteArray? = privateKey.encoded
        if (encoded == null) {
            // todo
        } else {
            val message = "SHA-256: " + BigInteger(md.digest(encoded)).toString(16)
            showToast(message)
        }
    }

    private fun showKeyguard() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val message = """
            isDeviceLocked: ${keyguardManager.isDeviceLocked}
            isDeviceSecure: ${keyguardManager.isDeviceSecure}
            isKeyguardLocked: ${keyguardManager.isKeyguardLocked}
            isKeyguardSecure: ${keyguardManager.isKeyguardSecure}
        """.trimIndent()
        showToast(message)
    }

    private fun authenticate(callback: BiometricPrompt.AuthenticationCallback) {
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("...title")
            .setSubtitle("...subtitle")
            .setDescription("...description")
            .also { builder ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    builder.setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL)
                } else {
                    builder.setDeviceCredentialAllowed(true)
                }
            }
            .setConfirmationRequired(true)
            .build()
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(this, executor, callback)
        prompt.authenticate(info)
    }

    private fun authenticate(onSucceeded: () -> Unit) {
        authenticate(
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    showToast("Authentication error: $errorCode $errString")
                }

                override fun onAuthenticationFailed() {
                    showToast("Authentication failed!")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSucceeded()
                }
            }
        )
    }

    private fun authenticate() {
        authenticate(
            onSucceeded = {
                showToast("Authentication succeeded.")
            }
        )
    }

    private fun getProviders() {
        val providers = Security.getProviders()
        val names = providers.map { it.name }
        showToast("providers: $names")
    }

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                RouterScreen()
            }
        }
    }
}
