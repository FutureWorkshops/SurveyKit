package com.quickbirdstudios.surveykit.survey

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.quickbirdstudios.surveykit.FinishReason
import com.quickbirdstudios.surveykit.SurveyTheme
import com.quickbirdstudios.surveykit.services.localization.LocalizationService
import com.quickbirdstudios.surveykit.backend.navigator.TaskNavigator
import com.quickbirdstudios.surveykit.result.StepResult
import com.quickbirdstudios.surveykit.result.TaskResult
import com.quickbirdstudios.surveykit.services.image_loader.ImageLoaderService
import com.quickbirdstudios.surveykit.services.network.NetworkService
import com.quickbirdstudios.surveykit.steps.Step

internal interface Survey {
    var onStepResult: (Step?, StepResult?) -> Unit
    var onSurveyFinish: (TaskResult, FinishReason) -> Unit

    fun start(
        childFragmentManager: FragmentManager,
        taskNavigator: TaskNavigator,
        surveyTheme: SurveyTheme,
        isRestarting: Boolean,
        lifecycleOwner: LifecycleOwner,
        imageLoaderService: ImageLoaderService,
        localizationService: LocalizationService,
        networkService: NetworkService
    )

    fun backPressed()
}
