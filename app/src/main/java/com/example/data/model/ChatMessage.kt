package com.example.data.model

import android.graphics.Bitmap

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val imageBitmap: Bitmap? = null,
    val recommendedProducts: List<Product> = emptyList(),
    val suggestedActions: List<String> = emptyList(),
    val isStreaming: Boolean = false
)
