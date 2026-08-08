package com.manomitra.app.feature.mood

enum class MoodType {
    VERY_HAPPY,
    HAPPY,
    NEUTRAL,
    SAD,
    VERY_SAD
}

enum class MoodSource {
    MANUAL,
    AI
}

data class MoodEntry(
    val id: String = "",
    val mood: String = "",
    val moodScore: Int = 3,
    val note: String = "",
    val source: String = MoodSource.MANUAL.name,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    companion object {
        fun fromString(moodStr: String): MoodType {
            return when (moodStr.lowercase()) {
                "happy" -> MoodType.VERY_HAPPY
                "calm" -> MoodType.HAPPY
                "neutral" -> MoodType.NEUTRAL
                "sad" -> MoodType.SAD
                "stressed" -> MoodType.VERY_SAD
                else -> {
                    try {
                        MoodType.valueOf(moodStr.uppercase())
                    } catch (e: Exception) {
                        MoodType.NEUTRAL
                    }
                }
            }
        }

        fun mapToScore(type: MoodType): Int {
            return when (type) {
                MoodType.VERY_HAPPY -> 5
                MoodType.HAPPY -> 4
                MoodType.NEUTRAL -> 3
                MoodType.SAD -> 2
                MoodType.VERY_SAD -> 1
            }
        }
    }
}
