/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.backend.helpers

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalizationService @Inject constructor(
    private val context: Context
) {

    companion object {
        var selectedProjectLocale: ProjectLocale? = null

        fun getTranslation(from: String): String {
            return selectedProjectLocale?.translations?.firstOrNull { it.from.equals(from, true) }?.to ?: from
        }

        fun getTranslationOrNull(from: String?): String? {
            return if (from == null) null else getTranslation(from)
        }
    }

    fun handleLocalisation(supportedLanguages: MutableList<ProjectLocale>) {
        val hasEnglish = supportedLanguages.any { projectLocale ->
            projectLocale.languageId.substring(0, 2).equals("en", true)
        }
        if (!hasEnglish) supportedLanguages.add(ProjectLocale("en", listOf()))

        val locales = context.resources.configuration.locales
        val languageIdList = supportedLanguages.map { it.languageId }.toTypedArray()
        val matchLocale = locales.getFirstMatch(languageIdList)

        selectedProjectLocale = if (matchLocale == null) {
            supportedLanguages.first()
        } else {
            // Exact or Fuzzy match
            val locale = matchLocale.toString()
            supportedLanguages.firstOrNull { it.languageId.equals(locale, true) }
                ?: supportedLanguages.first { it.languageId.substring(0, 2).equals(locale.substring(0, 2), true) }
        }
    }

}