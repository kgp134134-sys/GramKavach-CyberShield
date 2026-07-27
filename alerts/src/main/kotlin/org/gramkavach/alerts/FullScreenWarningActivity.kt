package org.gramkavach.alerts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.view.Gravity
import android.graphics.Color
import androidx.core.net.toUri

class FullScreenWarningActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        val score = intent.getIntExtra(EXTRA_SCORE, 0); val reasons = intent.getStringArrayListExtra(EXTRA_REASONS).orEmpty()
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(48, 80, 48, 48); setBackgroundColor(Color.rgb(82, 0, 0)); gravity = Gravity.CENTER_VERTICAL }
        layout.addView(TextView(this).apply { text = "STOP — PAYMENT MAY BE RISKY"; textSize = 26f; setTextColor(Color.WHITE) })
        layout.addView(TextView(this).apply { text = "Risk score: $score/100\n\n${reasons.joinToString("\n") { "• $it" }}\n\nDo not enter your UPI PIN or share an OTP."; textSize = 18f; setTextColor(Color.WHITE); setPadding(0, 32, 0, 32) })
        
        val reportButton = Button(this).apply { 
            text = "REPORT TO CYBER CELL (1930)"
            setBackgroundColor(Color.WHITE)
            setTextColor(Color.rgb(183, 28, 28))
            setOnClickListener { 
                val intent = Intent(Intent.ACTION_DIAL, "tel:1930".toUri())
                startActivity(intent)
            }
        }
        layout.addView(reportButton)
        
        layout.addView(android.view.View(this).apply { layoutParams = LinearLayout.LayoutParams(1, 32) }) // Spacer

        layout.addView(Button(this).apply { text = "I understand"; setOnClickListener { finish() } }); setContentView(layout)
    }
    companion object { const val EXTRA_SCORE = "score"; const val EXTRA_REASONS = "reasons" }
}
