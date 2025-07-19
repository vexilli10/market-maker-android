package com.wakaragames.marketmaker.data.models


import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

// The main serializable class for the entire game state.
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class GameState(
    val playerPortfolio: PlayerPortfolio,
    val candleHistory: List<CandleData>
    // Future fields like unlockedUpgrades, newsHistory, etc., will go here.
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
