package com.wakaragames.marketmaker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakaragames.marketmaker.data.UpgradesDatabase
import com.wakaragames.marketmaker.data.models.Upgrade
import com.wakaragames.marketmaker.data.models.UpgradeCategory
import com.wakaragames.marketmaker.viewmodels.GameViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun TechnologyScreen(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()

    // Filter all upgrades into lists by category
    val rdUpgrades = UpgradesDatabase.allUpgrades.values.filter { it.category == UpgradeCategory.R_AND_D }
    val marketingUpgrades = UpgradesDatabase.allUpgrades.values.filter { it.category == UpgradeCategory.MARKETING }
    val complianceUpgrades = UpgradesDatabase.allUpgrades.values.filter { it.category == UpgradeCategory.COMPLIANCE }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text(
            "Development Tree",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp),
            color = Color.White
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // R&D Column
            TechTreeColumn(
                modifier = Modifier.weight(1f),
                title = "R&D",
                upgrades = rdUpgrades,
                gameState = gameState,
                onPurchaseClicked = { viewModel.purchaseUpgrade(it) }
            )
            // Marketing Column
            TechTreeColumn(
                modifier = Modifier.weight(1f),
                title = "Marketing",
                upgrades = marketingUpgrades,
                gameState = gameState,
                onPurchaseClicked = { viewModel.purchaseUpgrade(it) }
            )
            // Compliance Column
            TechTreeColumn(
                modifier = Modifier.weight(1f),
                title = "Compliance",
                upgrades = complianceUpgrades,
                gameState = gameState,
                onPurchaseClicked = { viewModel.purchaseUpgrade(it) }
            )
        }
    }
}

@Composable
private fun TechTreeColumn(
    modifier: Modifier = Modifier,
    title: String,
    upgrades: List<Upgrade>,
    gameState: com.wakaragames.marketmaker.data.models.GameState,
    onPurchaseClicked: (String) -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            itemsIndexed(upgrades) { index, upgrade ->
                // Determine the state of the card
                val isPurchased = gameState.purchasedUpgradeIds.contains(upgrade.id)
                val isLocked = (upgrade.dependsOn != null && !gameState.purchasedUpgradeIds.contains(upgrade.dependsOn!!))
                val canAfford = gameState.playerPortfolio.cash >= upgrade.cost

                Box(contentAlignment = Alignment.TopCenter) {
                    // Connecting Line
                    if (index != upgrades.lastIndex) {
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxHeight() // This seems wrong for LazyColumn, will be constrained by Box
                                .height(80.dp) // A fixed height works better here
                                .width(2.dp)
                        )
                    }

                    UpgradeCard(
                        upgrade = upgrade,
                        isLocked = isLocked,
                        isPurchased = isPurchased,
                        canAfford = canAfford,
                        onPurchase = { onPurchaseClicked(upgrade.id) }
                    )
                }
            }
        }
    }
}


@Composable
private fun UpgradeCard(
    upgrade: Upgrade,
    isLocked: Boolean,
    isPurchased: Boolean,
    canAfford: Boolean,
    onPurchase: () -> Unit
) {
    val cardAlpha = if (isLocked) 0.5f else 1.0f
    val costColor = if (isPurchased || canAfford) Color.Green.copy(alpha = 0.7f) else Color.Red
    val cardBorderColor = if (isPurchased) Color(0xFF00C853) else Color.Gray.copy(alpha = 0.5f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .alpha(cardAlpha),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(upgrade.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(upgrade.description, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Effect: ${upgrade.effectDescription}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // --- STATE-DRIVEN BUTTON AREA ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cost Text
                Text(
                    NumberFormat.getCurrencyInstance(Locale.US).format(upgrade.cost),
                    fontWeight = FontWeight.Bold,
                    color = costColor
                )

                // Button / Status
                when {
                    isLocked -> {
                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray)
                    }
                    isPurchased -> {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Purchased", tint = Color(0xFF00C853))
                    }
                    else -> { // Available (Affordable or Unaffordable)
                        Button(
                            onClick = onPurchase,
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00C853),
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        ) {
                            Text("Purchase")
                        }
                    }
                }
            }
        }
    }
}