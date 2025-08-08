package com.wakaragames.marketmaker.data.models


import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

// The main serializable class for the entire game state.
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class GameState(
    val playerPortfolio: PlayerPortfolio,
    val candleHistory: List<CandleData>,
    val historicalCandleCount: Int,
    val purchasedUpgradeIds: Set<String>, // Tracks which upgrades have been bought
    val activeEffects: List<ActiveEffect>, // Tracks currently active timed effects
    val newsFeed: List<NewsItem>, // Holds all triggered news headlines
    val triggeredOneTimeEventIds: Set<String>, // Tracks which unique events have happened
    val currentGrowthRate: Float = 0.65f
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PlayerPortfolio(
    val cash: Double,
    val coins: Int
)

// This was previously the 'Candle' class. It's now public and serializable.
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CandleData(
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float
)
