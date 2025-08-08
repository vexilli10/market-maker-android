package com.wakaragames.marketmaker.data.models

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

// --- Enums to define the system's capabilities ---

enum class TriggerConditionType {
    MARKET_CAP_ABOVE,
    HISTORICAL_CANDLE_COUNT_ABOVE,
    HYPE_SCORE_ABOVE,
    UPGRADE_IS_PURCHASED,
    GLOBAL_INTEREST_RATE_ABOVE,
    ALWAYS // For events that can always trigger
}

// Re-using the same structure as Upgrades for simplicity
// A more complex system might have its own EffectType enum
enum class GameEffectType {
    PRICE_TREND_MODIFIER,
    HYPE_MODIFIER,
    NEGATIVE_EVENT_CHANCE_MODIFIER,
    INSTITUTIONAL_TRUST_MODIFIER,
    NONE
}

// --- Core Data Structures ---

/**
 * Represents a single condition that must be met for an event to have a chance to trigger.
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TriggerCondition(
    val type: TriggerConditionType,
    val value: Double = 0.0, // e.g., market cap amount, hype score
    val stringValue: String? = null // e.g., the ID of a required upgrade
)

/**
 * Represents a dynamic event that can occur in the game.
 * This is static game data.
 */
data class GameEvent(
    val id: String,
    val headline: String,
    val triggerConditions: List<TriggerCondition>,
    val triggerChance: Float, // e.g., 0.05 for a 5% chance per tick
    val isDummy: Boolean = false,
    val isOneTime: Boolean = false, // If true, it won't trigger again after success

    // Effect details (can be null for dummy events)
    val effectType: GameEffectType?,
    val effectValue: Float?,
    val effectDurationInCandles: Int?
)

/**
 * A simple data class for a news item in the feed.
 * This is part of the dynamic, saveable GameState.
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class NewsItem(
    val eventId: String,
    val headline: String,
    val timestamp: Int // The historical candle count when this news was generated
)