package com.wakaragames.marketmaker.data

import com.wakaragames.marketmaker.data.models.Upgrade
import com.wakaragames.marketmaker.data.models.UpgradeCategory

object UpgradesDatabase {

    val allUpgrades: Map<String, Upgrade> = listOf(
        // --- R&D Upgrades ---
        Upgrade(
            id = "rd_pos_consensus",
            name = "PoS Consensus Update",
            description = "Refactor the core protocol for efficiency and scalability.",
            category = UpgradeCategory.R_AND_D,
            cost = 2_000_000.0,
            effectDescription = "+20% base price growth for 100 candles.",
            effectDurationInCandles = 100,
            dependsOn = null
        ),
        Upgrade(
            id = "rd_quantum_encryption",
            name = "Quantum Encryption",
            description = "Implement next-generation security to deter attackers.",
            category = UpgradeCategory.R_AND_D,
            cost = 5_000_000.0,
            effectDescription = "-50% chance of negative hack/scandal events for 200 candles.",
            effectDurationInCandles = 200,
            dependsOn = "rd_pos_consensus"
        ),
        Upgrade(
            id = "rd_layer2_scaling",
            name = "Layer-2 Scaling Solution",
            description = "Boost network throughput and reduce transaction costs.",
            category = UpgradeCategory.R_AND_D,
            cost = 3_500_000.0,
            effectDescription = "+15% background Hype generation for 150 candles.",
            effectDurationInCandles = 150,
            dependsOn = "rd_layer2_scaling"
        ),

        // --- Marketing Upgrades ---
        Upgrade(
            id = "mkt_social_blitz",
            name = "Social Media Blitz",
            description = "Flood social platforms with targeted advertisements and influencer posts.",
            category = UpgradeCategory.MARKETING,
            cost = 750_000.0,
            effectDescription = "Instant +50 Hype and increased retail interest for 80 candles.",
            effectDurationInCandles = 80,
            dependsOn = null
        ),
        Upgrade(
            id = "mkt_viral_meme",
            name = "Viral \"Meme\" Campaign",
            description = "Attempt to capture the chaotic energy of the internet. High risk, high reward.",
            category = UpgradeCategory.MARKETING,
            cost = 1_000_000.0,
            effectDescription = "70% chance of +40 Hype for 100 candles, 30% chance of a small negative 'Cringe' event.",
            effectDurationInCandles = 100,
            dependsOn = "mkt_social_blitz"
        ),
        Upgrade(
            id = "mkt_stadium_rights",
            name = "Stadium Naming Rights",
            description = "Put our name on a major sports stadium for massive brand recognition.",
            category = UpgradeCategory.MARKETING,
            cost = 12_000_000.0,
            effectDescription = "Triggers a 'Mainstream Mania' event for 20 candles.",
            effectDurationInCandles = 20,
            dependsOn = "mkt_viral_meme"
        ),
        // --- Compliance Upgrades ---
        Upgrade(
            id = "cmp_offshore_foundation",
            name = "Offshore Foundation Setup",
            description = "Establish a legal entity in a jurisdiction with more 'flexible' financial laws.",
            category = UpgradeCategory.COMPLIANCE,
            cost = 1_500_000.0,
            effectDescription = "-50% transaction taxes for 300 candles, but slightly increases chance of 'Scandal' events.",
            effectDurationInCandles = 300,
            dependsOn = null
        ),
        Upgrade(
            id = "cmp_ex_regulator",
            name = "Hire Ex-Regulator",
            description = "Bring on a former regulator as a consultant for their invaluable insight and connections.",
            category = UpgradeCategory.COMPLIANCE,
            cost = 3_000_000.0,
            effectDescription = "Grants one-time use ability to nullify a 'Regulatory Crackdown' event.",
            effectDurationInCandles = 0,
            dependsOn = "cmp_offshore_foundation"
        ),
        Upgrade(
            id = "cmp_sandbox_approval",
            name = "Regulatory Sandbox Approval",
            description = "Work with regulators to gain approval for our technology in a controlled environment.",
            category = UpgradeCategory.COMPLIANCE,
            cost = 4_500_000.0,
            effectDescription = "Greatly increases Institutional Trust and provides immunity to minor negative regulatory news for 250 candles.",
            effectDurationInCandles = 250,
            dependsOn = "cmp_ex_regulator"
        )
    ).associateBy { it.id } // Create a Map<String, Upgrade> for easy lookup
}