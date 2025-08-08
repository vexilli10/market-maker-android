package com.wakaragames.marketmaker.data

import com.wakaragames.marketmaker.data.models.GameEvent
import com.wakaragames.marketmaker.data.models.GameEffectType
import com.wakaragames.marketmaker.data.models.TriggerCondition
import com.wakaragames.marketmaker.data.models.TriggerConditionType

object EventsDatabase {

    val allEvents: List<GameEvent> = listOf(

        // --- Dummy / Flavor Events ---
        GameEvent(
            id = "dummy_1",
            headline = "Can't believe the season finale of 'Galaxy Raiders' ended like that! #spoilers",
            triggerConditions = listOf(TriggerCondition(TriggerConditionType.ALWAYS)),
            triggerChance = 0.1f,
            isDummy = true,
            isOneTime = false,
            effectType = null,
            effectValue = 0f,
            effectDurationInCandles = 0
        ),
        GameEvent(
            id = "dummy_2",
            headline = "Is it just me or is coffee tasting extra good today? #caffeine",
            triggerConditions = listOf(TriggerCondition(TriggerConditionType.ALWAYS)),
            triggerChance = 0.1f,
            isDummy = true,
            isOneTime = false,
            effectType = null,
            effectValue = 0f,
            effectDurationInCandles = 0
        ),
        GameEvent(
            id = "dummy_3",
            headline = "Planning my vacation for next year. Any recommendations?",
            triggerConditions = listOf(TriggerCondition(TriggerConditionType.ALWAYS)),
            triggerChance = 0.1f,
            isDummy = true,
            isOneTime = false,
            effectType = null,
            effectValue = 0f,
            effectDurationInCandles = 0
        ),

        // --- Real Game Events ---
        GameEvent(
            id = "crisis_interest_rate",
            headline = "Global Central Banks unite to raise interest rates to 5% to combat persistent inflation. Analysts expect a market downturn.",
            triggerConditions = listOf(TriggerCondition(TriggerConditionType.GLOBAL_INTEREST_RATE_ABOVE, 5.0)),
            triggerChance = 0.1f,
            effectType = GameEffectType.PRICE_TREND_MODIFIER, effectValue = -0.10f, effectDurationInCandles = 200
        ),
        GameEvent(
            id = "hype_celeb_tweet",
            headline = "Just heard about \$YOUR_ASSET... looks intriguing. Might have to pick some up. 👀 #crypto #altcoin",
            triggerConditions = listOf(TriggerCondition(TriggerConditionType.MARKET_CAP_ABOVE, 10_000_000.0)),
            triggerChance = 0.02f,
            effectType = GameEffectType.HYPE_MODIFIER, effectValue = 50f, effectDurationInCandles = 0 // Instant
        ),
        GameEvent(
            id = "review_pos_update",
            headline = "TechFront Magazine publishes a glowing review of YourAsset, praising its recent 'PoS Update' as a 'major leap forward in efficiency and security'.",
            triggerConditions = listOf(TriggerCondition(TriggerConditionType.UPGRADE_IS_PURCHASED, stringValue = "rd_pos_consensus")),
            triggerChance = 0.2f, isOneTime = true, // Only happens once
            effectType = GameEffectType.INSTITUTIONAL_TRUST_MODIFIER, effectValue = 0.15f, effectDurationInCandles = 150
        ),
        GameEvent(
            id = "panic_flash_crash",
            headline = "BREAKING: Unexplained server outage at a major exchange has triggered a flash crash across the entire market! Trading paused.",
            triggerConditions = listOf(TriggerCondition(TriggerConditionType.ALWAYS)),
            triggerChance = 0.005f, // 0.5% chance, very rare
            effectType = GameEffectType.PRICE_TREND_MODIFIER, effectValue = -0.30f, effectDurationInCandles = 25
        )
    )
}