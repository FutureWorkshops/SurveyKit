package com.quickbirdstudios.surveykit.survey

import com.quickbirdstudios.surveykit.FinishReason
import com.quickbirdstudios.surveykit.SurveyTheme
import com.quickbirdstudios.surveykit.backend.helpers.LocalizationService
import com.quickbirdstudios.surveykit.backend.navigator.TaskNavigator
import com.quickbirdstudios.surveykit.result.StepResult
import com.quickbirdstudios.surveykit.result.TaskResult
import com.quickbirdstudios.surveykit.services.image_loader.ImageLoaderService
import com.quickbirdstudios.surveykit.steps.Step

internal interface Survey {
    var onStepResult: (Step?, StepResult?) -> Unit
    var onSurveyFinish: (TaskResult, FinishReason) -> Unit

    fun start(
        taskNavigator: TaskNavigator,
        surveyTheme: SurveyTheme,
        isRestarting: Boolean,
        imageLoaderService: ImageLoaderService,
        localizationService: LocalizationService
    )

    fun backPressed()
}
