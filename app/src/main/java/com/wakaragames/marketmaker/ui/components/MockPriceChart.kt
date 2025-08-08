package com.wakaragames.marketmaker.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakaragames.marketmaker.data.models.CandleData
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

// Color scheme
private val colorGreen = Color(0xFF00C853)
private val colorRed = Color(0xFFD50000)
private val colorGrid = Color.White.copy(alpha = 0.2f)
private val colorAxisText = Color.White.copy(alpha = 0.8f)

/**
 * A stateless chart composable that draws a list of CandleData.
 */
@Composable
internal fun MockPriceChart(
    modifier: Modifier = Modifier,
    candles: List<CandleData>,
    startIndex: Int
) {
    val animationPhase = remember { Animatable(1f) }

    LaunchedEffect(candles.size) {
        animationPhase.snapTo(0f)
        animationPhase.animateTo(1f, animationSpec = tween(durationMillis = 500))
    }

    val density = LocalDensity.current
    val textPaint = remember {
        Paint().apply {
            color = colorAxisText.toArgb()
            textAlign = Paint.Align.CENTER
            textSize = with(density) { 12.sp.toPx() }
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }
    }

    Canvas(modifier = modifier) {
        if (candles.isEmpty()) return@Canvas

        val yAxisAreaWidth = with(density) { 50.dp.toPx() }
        val xAxisAreaHeight = with(density) { 25.dp.toPx() }
        val chartWidth = size.width - yAxisAreaWidth
        val chartHeight = size.height - xAxisAreaHeight

        val minPrice = candles.minOf { it.low }
        val maxPrice = candles.maxOf { it.high }
        val priceRange = max(0.1f, maxPrice - minPrice)
        val candleWidth = chartWidth / 31f
        val xShift = if (candles.size < 30) 0f else candleWidth * animationPhase.value

        // Y-Axis drawing logic remains unchanged
        val yAxisLabels = generateYAxisLabels(minPrice, maxPrice)
        yAxisLabels.forEach { price ->
            val y = chartHeight * (1 - (price - minPrice) / priceRange)
            drawLine(
                color = colorGrid,
                start = Offset(0f, y),
                end = Offset(chartWidth, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 12f), 0f)
            )
            drawContext.canvas.nativeCanvas.drawText(
                "$${"%.2f".format(price)}",
                chartWidth + with(density) { 8.dp.toPx() },
                y + textPaint.textSize / 3,
                textPaint.apply { textAlign = Paint.Align.LEFT }
            )
        }


        // --- FIXED DYNAMIC X-AXIS LOGIC ---

        // 1. Calculate the start and end indices of the candles currently visible on screen.
        val firstVisibleLocalIndex = (xShift / candleWidth).toInt()
        val lastVisibleLocalIndex = ((xShift + chartWidth) / candleWidth).toInt().coerceAtMost(candles.size - 1)

        for (localIndex in firstVisibleLocalIndex..lastVisibleLocalIndex) {
            if (localIndex < 0) continue

            // Calculate the candle's true historical index
            val historicalIndex = startIndex + localIndex
            // Convert to a 1-indexed value for user display (Bug #2 Fix)
            val userVisibleIndex = historicalIndex + 1

            // Draw a label for every 5th candle (e.g., 5, 10, 15...)
            if (userVisibleIndex > 0 && userVisibleIndex % 5 == 0) {
                val labelText = "$userVisibleIndex"
                // The xPosition is still based on the LOCAL index
                val xPosition = (localIndex * candleWidth) + (candleWidth / 2) - xShift

                drawContext.canvas.nativeCanvas.drawText(
                    labelText,
                    xPosition,
                    chartHeight + xAxisAreaHeight,
                    textPaint.apply { textAlign = Paint.Align.CENTER }
                )
            }
        }


        // --- DRAW CANDLES --- (Unchanged)
        candles.forEachIndexed { index, candle ->
            val xOffset = index * candleWidth + (candleWidth / 2) - xShift
            if (xOffset > -candleWidth && xOffset < chartWidth + candleWidth) {
                drawCandle(this, candle, xOffset, candleWidth * 0.8f, minPrice, priceRange, chartHeight)
            }
        }
    }
}

// --- HELPER FUNCTIONS --- (Unchanged)
private fun DrawScope.drawCandle(
    drawScope: DrawScope,
    candle: CandleData,
    xOffset: Float,
    candleWidth: Float,
    minPrice: Float,
    priceRange: Float,
    chartHeight: Float
) {
    val isGreen = candle.close >= candle.open
    val candleColor = if (isGreen) colorGreen else colorRed
    fun priceToY(price: Float) = chartHeight * (1 - (price - minPrice) / priceRange)
    val highY = priceToY(candle.high)
    val lowY = priceToY(candle.low)
    val openY = priceToY(candle.open)
    val closeY = priceToY(candle.close)
    drawLine(color = candleColor, start = Offset(x = xOffset, y = highY), end = Offset(x = xOffset, y = lowY), strokeWidth = 2f)
    drawRect(color = candleColor, topLeft = Offset(x = xOffset - candleWidth / 2, y = min(openY, closeY)), size = androidx.compose.ui.geometry.Size(width = candleWidth, height = abs(openY - closeY).takeIf { it > 0f } ?: 1f))
}

private fun generateYAxisLabels(min: Float, max: Float, maxTicks: Int = 5): List<Float> {
    val range = max - min
    if (range <= 0) return listOf(min)
    val unroundedTickSize = range / (maxTicks - 1)
    val x = kotlin.math.ceil(kotlin.math.log10(unroundedTickSize.toDouble()) - 1).toFloat()
    val pow10x = 10.0.pow(x.toDouble()).toFloat()
    val roundedTickSize = kotlin.math.ceil(unroundedTickSize / pow10x) * pow10x
    val minTick = kotlin.math.floor(min / roundedTickSize) * roundedTickSize
    val maxTick = kotlin.math.ceil(max / roundedTickSize) * roundedTickSize
    val ticks = mutableListOf<Float>()
    var currentTick = minTick
    while (currentTick <= maxTick) {
        ticks.add(currentTick)
        currentTick += roundedTickSize
        if (ticks.size > maxTicks * 2) { return (0..maxTicks).map { i -> min + i * (max - min) / maxTicks } }
    }
    return ticks.filter { it >= min }
}