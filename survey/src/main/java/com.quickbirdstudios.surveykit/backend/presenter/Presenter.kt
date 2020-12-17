package com.quickbirdstudios.surveykit.backend.presenter

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.quickbirdstudios.surveykit.SurveyTheme
import com.quickbirdstudios.surveykit.result.StepResult
import com.quickbirdstudios.surveykit.services.MobileWorkflowServices
import com.quickbirdstudios.surveykit.steps.Step

interface Presenter {
    val context: Context
    val fragmentContainerView: FragmentContainerView
    val childFragmentManager: FragmentManager
    val surveyTheme: SurveyTheme
    val lifecycleOwner: LifecycleOwner
    val mobileWorkflowServices: MobileWorkflowServices

    suspend operator fun invoke(
        transition: Transition,
        step: Step,
        stepResult: StepResult?
    ): NextAction

    fun triggerBackOnCurrentView()

    enum class Transition {
        None, SlideFromRight, SlideFromLeft;
    }
}
