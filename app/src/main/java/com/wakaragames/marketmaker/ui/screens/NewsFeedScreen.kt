package com.wakaragames.marketmaker.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wakaragames.marketmaker.R
import com.wakaragames.marketmaker.ui.theme.MarketMakerTheme

/**
 * A simple data class representing a single, styled post in the news feed.
 */
data class NewsPost(
    val id: String,
    val authorName: String,
    val content: String
)

/**
 * The main UI screen for displaying a feed of news events.
 * @param newsPosts The list of posts to display. The list should be ordered
 * with the oldest item at index 0.
 */
@Composable
fun NewsFeedScreen(
    modifier: Modifier = Modifier,
    newsPosts: List<NewsPost>
) {
    // A LazyColumn is used for performance with long lists.
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        // CRITICAL: This anchors the scroll to the bottom and builds the list upwards.
        // When a new item is added to the end of the `newsPosts` list, it will
        // appear at the top of the screen.
        reverseLayout = true
    ) {
        items(
            items = newsPosts,
            key = { post -> post.id } // Use a unique ID as the key for performance
        ) { post ->
            NewsPostItem(post = post)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/**
 * A reusable Composable for displaying a single post in the feed.
 */
@Composable
private fun NewsPostItem(
    post: NewsPost,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Spacer(modifier = Modifier.width(12.dp))

            // Content Column
            Column {
                Text(
                    text = post.authorName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// --- PREVIEW --- //

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewsFeedScreenPreview() {
    // Create a dummy list of posts for the preview.
    // IMPORTANT: The oldest post ("Launched!") is at index 0. `reverseLayout`
    // will correctly place it at the bottom of the visible list.
    val dummyNewsPosts = listOf(
        NewsPost(
            id = "1",
            authorName = "Market Maker System",
            content = "Project 'Market Maker' has launched!"
        ),
        NewsPost(
            id = "2",
            authorName = "CryptoJournalist",
            content = "A new player has entered the market. Analysts are watching closely to see how it performs in its crucial early stages."
        ),
        NewsPost(
            id = "3",
            authorName = "MegaHypeInfluencer",
            content = "Just heard about \$MKR... looks intriguing. Might have to pick some up. 👀 #crypto #altcoin"
        )
    )

    MarketMakerTheme {
        NewsFeedScreen(newsPosts = dummyNewsPosts)
    }
}