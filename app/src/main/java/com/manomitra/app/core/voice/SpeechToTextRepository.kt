package com.manomitra.app.core.voice

import java.io.File

interface SpeechToTextRepository {
    suspend fun transcribeAudio(audioFile: File, languageMode: String): Result<String>
}
