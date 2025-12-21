package com.baharlou.pedalpace.presentation.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baharlou.pedalpace.ui.theme.NightBlue
import com.baharlou.pedalpace.ui.theme.PedalPaceTheme
import com.baharlou.pedalpace.ui.theme.TextPrimary
import kotlinx.coroutines.isActive
import kotlin.random.Random

/**
 *  weather animation types
 */
enum class WeatherType { RAIN, SNOW, SUNNY, CLOUDY }

/**
 * Data class representing a single falling element
 */
data class WeatherParticle(
    var x: Float,          // Horizontal position (0.0 to 1.0)
    var y: Float,          // Vertical position (0.0 to 1.0)
    val speed: Float,      // Falling speed
    val size: Float,       // Size/Thickness
    val alpha: Float,      // Transparency for depth effect
    val drift: Float       // Side-to-side movement (for snow)
)

@Composable
fun WeatherEffectBackground(
    weatherType: WeatherType,
    modifier: Modifier = Modifier
) {
    // no need for particle animations for Sunny/Cloudy (static icons suffice)
    if (weatherType == WeatherType.SUNNY || weatherType == WeatherType.CLOUDY) return

    // State list of particles to avoid object allocation in the loop
    val particles = remember(weatherType) {
        mutableStateListOf<WeatherParticle>().apply {
            repeat(70) { // Number of particles
                add(createRandomParticle(initialY = Random.nextFloat()))
            }
        }
    }

    // High-performance animation loop
    LaunchedEffect(weatherType) {

       /* if (particles.isEmpty()) {
            repeat(70) {
                particles.add(createRandomParticle(initialY = Random.nextFloat()))
            }
        }
*/
        while (isActive) {
            withFrameNanos { _ ->
                for (i in particles.indices) {
                    val p = particles[i]

                    // Update vertical position
                    p.y += p.speed

                    // Add drift for snow
                    if (weatherType == WeatherType.SNOW) {
                        p.x += p.drift
                    }

                    // Recycle particle to top when it leaves the bottom
                    if (p.y > 1.1f) {
                        p.y = -0.1f
                        p.x = Random.nextFloat()
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val color = if (weatherType == WeatherType.RAIN) {
            //Color(0xFF031933).copy(alpha = 0.6f) // Blueish rain
            Color(0xFF60A5FA).copy(alpha = 0.6f) // Blueish rain
        } else {
            Color.White.copy(alpha = 0.8f) // White snow
            //Color.Red//.copy(alpha = 0.8f) // White snow
        }

        particles.forEach { particle ->
            drawParticle(particle, color, weatherType)
        }
    }
}

/**
 * Creates a particle with randomized properties for a natural look
 */
private fun createRandomParticle(initialY: Float = -0.1f): WeatherParticle {
    return WeatherParticle(
        x = Random.nextFloat(),
        y = initialY,
        speed = 0.006f + Random.nextFloat() * 0.012f,
        size = 1f + Random.nextFloat() * 4f,
        alpha = 0.2f + Random.nextFloat() * 0.6f,
        drift = (Random.nextFloat() - 0.5f) * 0.002f // Slight horizontal movement
    )
}

/**
 * Specialized drawing logic for different weather types
 */
private fun DrawScope.drawParticle(
    particle: WeatherParticle,
    color: Color,
    type: WeatherType
) {
    val canvasX = particle.x * size.width
    val canvasY = particle.y * size.height

    if (type == WeatherType.RAIN) {
        // Rain: Long, thin slanted lines
        drawLine(
            color = color,
            start = Offset(canvasX, canvasY),
            end = Offset(canvasX - 3f, canvasY + 18f),
            strokeWidth = 2f
        )
    } else {
        // Snow: Soft circles
        drawCircle(
            color = color,
            radius = particle.size,
            center = Offset(canvasX, canvasY)
        )
    }
}


@Preview(showBackground = true, name = "Rain Animation")
@Composable
private fun PreviewRainEffect() {
    PedalPaceTheme {
        // We use a dark background so the particles are visible
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(TextPrimary)
        ) {
            WeatherEffectBackground(weatherType = WeatherType.RAIN)

            Text(
                "Rain Effect Active",
                color = Color.White,
                modifier = Modifier.padding(16.dp),
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, name = "Snow Animation")
@Composable
private fun PreviewSnowEffect() {
    PedalPaceTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(NightBlue)
        ) {
            WeatherEffectBackground(weatherType = WeatherType.SNOW)

            Text(
                "Snow Effect Active",
                color = Color.White,
                modifier = Modifier.padding(16.dp),
                fontSize = 12.sp
            )
        }
    }
}