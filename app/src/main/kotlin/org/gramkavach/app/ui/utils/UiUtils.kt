package org.gramkavach.app.ui.utils

import androidx.compose.ui.graphics.Color
import org.gramkavach.app.ui.theme.*

object UiUtils {
    fun getRiskColor(score: Int): Color {
        return when {
            score >= 60 -> HighRed
            score >= 40 -> ModerateOrange
            score >= 15 -> CautionAmber
            else -> SafeGreen
        }
    }
}
