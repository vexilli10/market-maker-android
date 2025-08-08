package com.wakaragames.marketmaker.data.models

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

enum class UpgradeCategory {
    MARKETING,
    R_AND_D,
    COMPLIANCE
}

/**
 * Represents a single, purchasable upgrade in the development tree.
 * This is static game data.
 */
data class Upgrade(
    val id: String,
    val name: String,
    val description: String,
    val category: UpgradeCategory,
    val cost: Double,
    val effectDescription: String,
    val effectDurationInCandles: Int,// 0 for instant/permanent
    val dependsOn: String?
)

/**
 * Represents an upgrade's effect that is currently active and ticking down.
 * This is part of the dynamic, saveable GameState.
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ActiveEffect(
    val upgradeId: String, // To know which upgrade this came from
    var durationRemainingInCandles: Int
)