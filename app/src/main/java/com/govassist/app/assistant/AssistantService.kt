package com.govassist.app.assistant

/**
 * AssistantService is a placeholder for the future AI assistant integration.
 *
 * Nothing in this file is implemented yet. It exists purely so the rest of
 * the app (voice button, text mode, form screens) can already be wired
 * against a stable interface, meaning that when real AI / speech features
 * are added later, screens will NOT need to be rewritten — only this
 * service (and its implementation) will change.
 *
 * Planned responsibilities (future work, NOT implemented now):
 *  - Accepting user input, either as recognised speech text or typed text
 *  - Sending that input to an AI model (e.g. via the Anthropic API)
 *  - Receiving a structured response describing what to say back to the user
 *    and/or what action to take (navigate, fill a field, confirm, etc.)
 *  - Triggering text-to-speech playback of responses
 *  - Helping the user step through government forms field-by-field
 */

/** A single user-facing turn returned by the assistant. */
data class AssistantResponse(
    val message: String,
    val action: AssistantAction = AssistantAction.None
)

/** Placeholder set of actions the assistant could eventually trigger. */
sealed class AssistantAction {
    data object None : AssistantAction()
    data class Navigate(val destination: String) : AssistantAction()
    data class FillField(val fieldId: String, val value: String) : AssistantAction()
    data object ConfirmRequired : AssistantAction()
}

interface AssistantService {

    /** Send free-form text (from speech-to-text or the text mode screen) to the assistant. */
    suspend fun sendMessage(userInput: String): AssistantResponse

    /** Start listening for spoken input. Not implemented in this prototype. */
    fun startListening()

    /** Stop listening for spoken input. Not implemented in this prototype. */
    fun stopListening()
}

/**
 * Temporary stub implementation.
 * Returns a fixed placeholder response so the rest of the app can call
 * this service without crashing, ahead of real AI/speech integration.
 */
class PlaceholderAssistantService : AssistantService {

    override suspend fun sendMessage(userInput: String): AssistantResponse {
        return AssistantResponse(
            message = "Voice assistant coming soon.",
            action = AssistantAction.None
        )
    }

    override fun startListening() {
        // Intentionally left blank — speech-to-text not implemented yet.
    }

    override fun stopListening() {
        // Intentionally left blank — speech-to-text not implemented yet.
    }
}
