package com.example.contactphone.util

object HangulUtils {
    private const val HANGUL_BASE = 0xAC00
    private const val HANGUL_END = 0xD7A3
    
    private val CHOSUNG = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 
        'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    fun getChosung(c: Char): Char {
        if (c.code in HANGUL_BASE..HANGUL_END) {
            val chosungIndex = (c.code - HANGUL_BASE) / (21 * 28)
            return CHOSUNG[chosungIndex]
        }
        return c
    }

    fun getChosungString(text: String): String {
        return text.map { getChosung(it) }.joinToString("")
    }

    fun match(name: String, query: String): Boolean {
        if (query.isEmpty()) return true
        
        // 1. Standard text search
        if (name.contains(query, ignoreCase = true)) return true
        
        // 2. Chosung search
        val nameChosung = getChosungString(name)
        return nameChosung.contains(query, ignoreCase = true)
    }
}
