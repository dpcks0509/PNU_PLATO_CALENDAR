package pusan.university.plato_calendar.data.local.database

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import java.io.IOException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class LoginCredentialsDataStore
@Inject
constructor(
    private val context: Context,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = LOGIN_CREDENTIALS_NAME)

    val loginCredentials: Flow<LoginCredentials?> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val storedUserName = preferences[USER_NAME]
                val storedPassword = preferences[PASSWORD]

                if (storedUserName.isNullOrBlank() || storedPassword.isNullOrBlank()) {
                    return@map null
                }

                val decryptedUserName = decrypt(storedUserName)
                val decryptedPassword = decrypt(storedPassword)

                if (!decryptedUserName.isNullOrBlank() && !decryptedPassword.isNullOrBlank()) {
                    LoginCredentials(userName = decryptedUserName, password = decryptedPassword)
                } else null
            }

    suspend fun saveLoginCredentials(loginCredentials: LoginCredentials) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = encrypt(loginCredentials.userName)
            preferences[PASSWORD] = encrypt(loginCredentials.password)
        }
    }

    suspend fun deleteLoginCredentials() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_NAME)
            preferences.remove(PASSWORD)
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) return existingKey

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val parameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(parameterSpec)
        return keyGenerator.generateKey()
    }

    private fun encrypt(plainText: String): String {
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = ByteArray(iv.size + cipherBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(cipherBytes, 0, combined, iv.size, cipherBytes.size)
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    private fun decrypt(base64CipherText: String): String? {
        return runCatching {
            val allBytes = Base64.decode(base64CipherText, Base64.NO_WRAP)
            if (allBytes.size <= 12) return null
            val iv = allBytes.copyOfRange(0, 12)
            val cipherBytes = allBytes.copyOfRange(12, allBytes.size)
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(AES_MODE)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, Charsets.UTF_8)
        }.getOrNull()
    }

    companion object {
        private const val LOGIN_CREDENTIALS_NAME = "login_credentials"
        private val USER_NAME = stringPreferencesKey("user_name")
        private val PASSWORD = stringPreferencesKey("password")
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "plato_login_credentials_key"
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH_BITS = 128
    }
}