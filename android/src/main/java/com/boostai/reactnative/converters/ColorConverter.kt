package com.boostai.reactnative.converters

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 * Utility for converting colors between hex strings and Android ColorInt
 */
object ColorConverter {
    /**
     * Convert a hex color string to Android ColorInt
     * @param hex Hex string in format "#RGB", "#RRGGBB", or "#AARRGGBB"
     * @return Android ColorInt value
     * @throws IllegalArgumentException if hex string is invalid
     */
    @ColorInt
    fun hexToColorInt(hex: String?): Int? {
        if (hex.isNullOrBlank()) {
            return null
        }

        return try {
            Color.parseColor(hex)
        } catch (e: IllegalArgumentException) {
            // Invalid hex color format
            throw IllegalArgumentException("Invalid hex color: $hex", e)
        }
    }

    /**
     * Convert Android ColorInt to hex string
     * @param color Android ColorInt value
     * @return Hex string in format "#AARRGGBB"
     */
    fun colorIntToHex(@ColorInt color: Int?): String? {
        if (color == null) {
            return null
        }

        return String.format("#%08X", color)
    }

    /**
     * Convert Android ColorInt to 6-digit hex string (no alpha)
     * @param color Android ColorInt value
     * @return Hex string in format "#RRGGBB"
     */
    fun colorIntToHexNoAlpha(@ColorInt color: Int?): String? {
        if (color == null) {
            return null
        }

        return String.format("#%06X", 0xFFFFFF and color)
    }
}
