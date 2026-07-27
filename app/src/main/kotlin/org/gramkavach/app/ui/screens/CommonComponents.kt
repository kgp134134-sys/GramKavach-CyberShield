package org.gramkavach.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gramkavach.app.R
import org.gramkavach.app.ui.theme.*
import kotlin.time.Duration.Companion.seconds

@Composable
fun RangoliPattern(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Rangoli")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Rotation"
    )

    Canvas(modifier = modifier) {
        val center = center
        val radius = size.minDimension / 2.5f
        val petals = 8
        val petalAngle = 360f / petals

        withTransform({
            rotate(rotation, center)
        }) {
            for (i in 0 until petals) {
                val angle = i * petalAngle
                drawArc(
                    color = SaffronDeep.copy(alpha = 0.3f),
                    startAngle = angle,
                    sweepAngle = petalAngle / 2f,
                    useCenter = true,
                    topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )
                drawCircle(
                    color = EarthTerracotta.copy(alpha = 0.2f),
                    radius = radius / 4f,
                    center = androidx.compose.ui.geometry.Offset(
                        center.x + (radius * 0.7f * kotlin.math.cos(Math.toRadians(angle.toDouble()))).toFloat(),
                        center.y + (radius * 0.7f * kotlin.math.sin(Math.toRadians(angle.toDouble()))).toFloat()
                    )
                )
            }
            drawCircle(color = SaffronDeep.copy(alpha = 0.1f), radius = radius, style = Stroke(2.dp.toPx()))
        }
    }
}

@Composable 
fun Splash(onReady: () -> Unit) { 
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(1.5.seconds); onReady() }
    Box(Modifier.fillMaxSize().background(CreamWarm), contentAlignment = Alignment.Center) { 
        RangoliPattern(Modifier.size(400.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(160.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displayMedium, color = SaffronDeep, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
            Text(stringResource(R.string.slogan_india), style = MaterialTheme.typography.titleMedium, color = EarthTerracotta, fontWeight = FontWeight.Bold)
        }
    } 
}
