/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.services.network

import com.quickbirdstudios.surveykit.backend.local.SecurePreferences
import javax.inject.Inject

class NetworkService @Inject constructor(
    private val securePreferences: SecurePreferences
) : INetworkService {

    override fun setAuthToken(token: String) {
        securePreferences.setAuthToken(token)
    }

}