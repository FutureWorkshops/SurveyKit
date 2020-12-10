/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.backend.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.util.*
import javax.inject.Inject

class SecurePreferences @Inject constructor(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val encryptedSharedPreferences: SharedPreferences

    init {
        encryptedSharedPreferences = EncryptedSharedPreferences
            .create(
                BLINK_SECURE_PREFERENCES_FILE_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }

    fun clear() = encryptedSharedPreferences.edit { clear() }

    fun setAuthToken(authToken: String) = encryptedSharedPreferences.edit {
        putString(KEY_AUTH_TOKEN, authToken)
    }

    fun getAuthToken(): String = encryptedSharedPreferences.getString(KEY_AUTH_TOKEN, "") ?: ""

    fun getVisitorToken(): UUID {
        val value = encryptedSharedPreferences.getString(VISITOR_TOKEN, "") ?: ""

        return if (value.isEmpty()) {
            val newToken = UUID.randomUUID()
            encryptedSharedPreferences.edit {
                putString(VISITOR_TOKEN, newToken.toString())
            }
            newToken
        } else {
            UUID.fromString(value)
        }
    }

    companion object {
        private const val BLINK_SECURE_PREFERENCES_FILE_NAME = "mww_secure_preferences"
        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
        private const val VISITOR_TOKEN = "VISITOR_TOKEN"
    }
}
