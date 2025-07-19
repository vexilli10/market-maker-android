package com.wakaragames.marketmaker.data.persistence

import android.content.Context
import com.wakaragames.marketmaker.data.models.GameState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

/**
 * Handles all file I/O for saving and loading the game state.
 */
object GameStateManager {

    private const val SAVE_FILE_NAME = "market_maker_save.json"

    fun saveGameState(context: Context, gameState: GameState) {
        val file = File(context.filesDir, SAVE_FILE_NAME)
        val jsonString = Json.encodeToString(gameState)
        file.writeText(jsonString)
    }

    fun loadGameState(context: Context): GameState? {
        return try {
            val file = File(context.filesDir, SAVE_FILE_NAME)
            val jsonString = file.readText()
            Json.decodeFromString<GameState>(jsonString)
        } catch (e: FileNotFoundException) {
            null // No save file exists
        } catch (e: Exception) {
            e.printStackTrace()
            null // Error during deserialization
        }
    }

    fun hasSavedGame(context: Context): Boolean {
        return File(context.filesDir, SAVE_FILE_NAME).exists()
    }

    fun deleteSavedGame(context: Context) {
        val file = File(context.filesDir, SAVE_FILE_NAME)
        if (file.exists()) {
            file.delete()
        }
    }
}