package com.baharlou.pedalpace.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()

            // Background Track
            drawArc(
                color = Color(0xFFF1F5F9),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )

            // Progress
            drawArc(
                color = Color(0xFF22C55E),
                startAngle = -90f,
                sweepAngle = (score / 100f) * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$score",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "%",
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewCircularProgressBar() {
    PedalPaceTheme {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressBar(
                score = 85
            )
        }
    }
}

@Preview(showBackground = true, name = "Lower Score")
@Composable
private fun PreviewCircularProgressBarLow() {
    PedalPaceTheme {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressBar(
                score = 32
            )
        }
    }
}