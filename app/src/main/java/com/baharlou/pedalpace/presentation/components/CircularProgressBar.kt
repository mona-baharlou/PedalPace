package com.baharlou.pedalpace.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baharlou.pedalpace.ui.theme.PedalPaceTheme

@Composable
fun CircularProgressBar(
    score: Int,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = (score / 100f) * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$score",
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "%",
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

// --- UPDATED PREVIEWS ---

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun PreviewCircularProgressBarTheme() {
    PedalPaceTheme {
        androidx.compose.material3.Surface(
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressBar(score = 85)
            }
        }
    }
}