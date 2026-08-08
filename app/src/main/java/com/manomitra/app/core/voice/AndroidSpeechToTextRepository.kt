package com.manomitra.app.core.voice

import java.io.File

class AndroidSpeechToTextRepository : SpeechToTextRepository {
    override suspend fun transcribeAudio(audioFile: File, languageMode: String): Result<String> {
        return Result.failure(UnsupportedOperationException("Android SpeechRecognizer does not support transcribing pre-recorded audio files. Use VoiceInputManager directly for live speech input."))
    }
}
