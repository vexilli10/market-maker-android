package com.wakaragames.marketmaker.ui.components

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * Calculates a list of "nice" values for a chart axis based on a data range.
 * This ensures the axis labels are clean, round numbers (e.g., 5, 10, 15 instead of 6.3, 11.3, 16.3).
 *
 * @param min The minimum value in the data range.
 * @param max The maximum value in the data range.
 * @param maxTicks The desired maximum number of labels (ticks) on the axis.
 * @return A list of Float values representing the calculated axis labels.
 */
/*
fun generateYAxisLabels(min: Float, max: Float, maxTicks: Int = 5): List<Float> {
    val range = max - min
    if (range <= 0) return listOf(min)

    val unroundedTickSize = range / (maxTicks - 1)
    val x = ceil(log10(unroundedTickSize.toDouble()) - 1).toFloat()
    val pow10x = 10.0.pow(x.toDouble()).toFloat()
    val roundedTickSize = ceil(unroundedTickSize / pow10x) * pow10x

    val minTick = floor(min / roundedTickSize) * roundedTickSize
    val maxTick = ceil(max / roundedTickSize) * roundedTickSize

    val ticks = mutableListOf<Float>()
    var currentTick = minTick
    while (currentTick <= maxTick) {
        ticks.add(currentTick)
        currentTick += roundedTickSize
        if (ticks.size > maxTicks * 2) { // Safety break
            return (0..maxTicks).map { i -> min + i * (max - min) / maxTicks }
        }
    }
    return ticks.filter { it >= min }
}

 */