package com.quickbirdstudios.surveykit.steps

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.step.StepView
import com.quickbirdstudios.surveykit.result.StepResult
import com.quickbirdstudios.surveykit.services.MobileWorkflowServices

interface Step {
    val isOptional: Boolean
    val id: StepIdentifier
    val uuid: String
    fun createView(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices,
        lifecycleOwner: LifecycleOwner
    ): StepView
}
