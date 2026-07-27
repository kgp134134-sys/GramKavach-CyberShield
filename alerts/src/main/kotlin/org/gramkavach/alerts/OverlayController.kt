package org.gramkavach.alerts

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import android.graphics.drawable.GradientDrawable
import android.animation.ValueAnimator
import dagger.hilt.android.qualifiers.ApplicationContext
import org.gramkavach.core.logging.KavachLogger
import org.gramkavach.domain.model.RiskAssessment
import org.gramkavach.domain.model.RiskLevel
import javax.inject.Inject
import javax.inject.Singleton

/** Manages a floating "Safety Badge" overlay to warn users over other apps. */
@Singleton
class OverlayController @Inject constructor(@ApplicationContext private val context: Context) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    private var animator: ValueAnimator? = null

    fun show(risk: RiskAssessment) {
        KavachLogger.d("Showing overlay: ${risk.level}")
        if (overlayView != null) {
            update(risk)
            return
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 100 // Padding from top
        }

        // Note: Using a simple FrameLayout based UI since we are in a library without direct access to app resources
        val root = FrameLayout(context).apply {
            setPadding(32, 16, 32, 16)
        }
        
        overlayView = root
        update(risk)
        
        try {
            windowManager.addView(overlayView, params)
            KavachLogger.i("Overlay added to WindowManager")
        } catch (_: Exception) {
            overlayView = null
        }
    }

    private fun update(risk: RiskAssessment) {
        val root = (overlayView as? FrameLayout) ?: return
        root.removeAllViews()
        
        val color = when (risk.level) {
            RiskLevel.CRITICAL, RiskLevel.HIGH -> 0xFFEF4444.toInt() // HighRed
            RiskLevel.MODERATE -> 0xFFF97316.toInt() // ModerateOrange
            RiskLevel.CAUTION -> 0xFFFFC107.toInt() // CautionAmber
            else -> 0xFF10B981.toInt() // SafeGreen
        }

        val iconRes = when (risk.level) {
            RiskLevel.SAFE -> R.drawable.ic_safe_shield
            else -> R.drawable.ic_warning_triangle
        }

        val background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 100f // Pills shape
            setColor(color)
        }

        val content = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(32, 16, 48, 16)
            setBackground(background)
            alpha = 0.95f
        }
        
        val icon = ImageView(context).apply {
            setImageResource(iconRes)
            layoutParams = LinearLayout.LayoutParams(60, 60)
        }

        val text = TextView(context).apply {
            text = if (risk.level == RiskLevel.SAFE) " GramKavach: SECURE " else " GramKavach: ${risk.level} RISK "
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(16, 0, 0, 0)
            textSize = 16f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        
        content.addView(icon)
        content.addView(text)
        root.addView(content)
        
        if (risk.level != RiskLevel.SAFE) {
            animator = ValueAnimator.ofFloat(0.7f, 1.0f).apply {
                duration = 800
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                addUpdateListener { content.alpha = it.animatedValue as Float }
                start()
            }
        } else {
            animator?.cancel()
            animator = null
        }

        // Auto-dismiss after 3 seconds if it's safe
        if (risk.level == RiskLevel.SAFE) {
            root.postDelayed({ dismiss() }, 3000)
        }
    }

    fun dismiss() {
        animator?.cancel()
        animator = null
        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {}
            overlayView = null
        }
    }
}
