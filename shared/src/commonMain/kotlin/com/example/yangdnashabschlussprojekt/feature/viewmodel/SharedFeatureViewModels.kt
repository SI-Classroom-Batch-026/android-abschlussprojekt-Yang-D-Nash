package com.example.yangdnashabschlussprojekt.feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.feature.model.SharedHistoryItem
import com.example.yangdnashabschlussprojekt.feature.model.SharedUser
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionRepository
import com.example.yangdnashabschlussprojekt.feature.repository.CaptureGateway
import com.example.yangdnashabschlussprojekt.feature.repository.HistoryGateway
import com.example.yangdnashabschlussprojekt.feature.repository.OnboardingGateway
import com.example.yangdnashabschlussprojekt.feature.repository.SessionGateway
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SharedWelcomeViewModel(
    sessionGateway: SessionGateway,
    private val onboardingGateway: OnboardingGateway
) : ViewModel() {
    val displayName: StateFlow<String> = sessionGateway.currentUser
        .map { it?.displayName ?: "Gast" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = "Gast"
        )

    fun restartOnboarding() {
        onboardingGateway.restartOnboarding()
    }
}

class SharedSettingsViewModel(
    private val sessionGateway: SessionGateway
) : ViewModel() {
    val currentUser: StateFlow<SharedUser?> = sessionGateway.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _authResult = MutableStateFlow<String?>(null)
    val authResult: StateFlow<String?> = _authResult.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val error = sessionGateway.login(email.trim(), password.trim())
            _authResult.value = error ?: "Erfolgreich eingeloggt."
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionGateway.logout()
            _authResult.value = "Du wurdest abgemeldet."
        }
    }

    fun showMessage(message: String) {
        _authResult.value = message
    }

    fun clearMessage() {
        _authResult.value = null
    }
}

class SharedRegistrationViewModel(
    private val sessionGateway: SessionGateway
) : ViewModel() {
    val currentUser: StateFlow<SharedUser?> = sessionGateway.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _registrationMessage = MutableStateFlow<String?>(null)
    val registrationMessage: StateFlow<String?> = _registrationMessage.asStateFlow()

    private val _registrationSucceeded = MutableStateFlow(false)
    val registrationSucceeded: StateFlow<Boolean> = _registrationSucceeded.asStateFlow()

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            val error = sessionGateway.register(
                email = email.trim(),
                password = password.trim(),
                displayName = displayName.trim()
            )
            _registrationSucceeded.value = error == null
            _registrationMessage.value = error ?: "Konto erstellt. Du bist jetzt angemeldet."
        }
    }

    fun clearResult() {
        _registrationSucceeded.value = false
        _registrationMessage.value = null
    }
}

class SharedHistoryViewModel(
    private val historyGateway: HistoryGateway
) : ViewModel() {
    val historyState: StateFlow<List<SharedHistoryItem>> = historyGateway.history
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun clearAllHistory() {
        viewModelScope.launch {
            historyGateway.clearAll()
        }
    }

    fun deleteHistoryItem(item: SharedHistoryItem) {
        viewModelScope.launch {
            historyGateway.delete(item)
        }
    }
}

class SharedArViewModel(
    private val cameraManager: CameraManager,
    private val cloudVisionRepository: CloudVisionRepository
) : ViewModel() {
    val platformName: String = cameraManager.platformName
    val canCaptureImages: Boolean = cameraManager.supportsDirectCapture
    val canImportImages: Boolean = cameraManager.supportsImageImport

    private var selectedImageBase64: String? = null

    private val _selectedImageName = MutableStateFlow<String?>(null)
    val selectedImageName: StateFlow<String?> = _selectedImageName.asStateFlow()

    private val _selectedImagePath = MutableStateFlow<String?>(null)
    val selectedImagePath: StateFlow<String?> = _selectedImagePath.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _primaryLabel = MutableStateFlow<String?>(null)
    val primaryLabel: StateFlow<String?> = _primaryLabel.asStateFlow()

    private val _detectedCandidates = MutableStateFlow<List<String>>(emptyList())
    val detectedCandidates: StateFlow<List<String>> = _detectedCandidates.asStateFlow()

    private val _isCapturingImage = MutableStateFlow(false)
    val isCapturingImage: StateFlow<Boolean> = _isCapturingImage.asStateFlow()

    private val _isImportingImage = MutableStateFlow(false)
    val isImportingImage: StateFlow<Boolean> = _isImportingImage.asStateFlow()

    private val _isAnalyzingScene = MutableStateFlow(false)
    val isAnalyzingScene: StateFlow<Boolean> = _isAnalyzingScene.asStateFlow()

    fun openCamera() {
        _statusMessage.value = cameraManager.openCamera()
    }

    fun captureImage() {
        if (!canCaptureImages) {
            _statusMessage.value = "Direkte Bildaufnahme ist auf $platformName noch nicht verfuegbar."
            return
        }

        viewModelScope.launch {
            _isCapturingImage.value = true
            val capturedImage = runCatching {
                cameraManager.captureImage()
            }.getOrElse { error ->
                _statusMessage.value = error.message ?: "Bildaufnahme fehlgeschlagen."
                _isCapturingImage.value = false
                return@launch
            }

            handleSelectedImage(
                image = capturedImage,
                emptyMessage = "Keine Aufnahme erstellt.",
                successPrefix = "Aufnahme bereit"
            )
            _isCapturingImage.value = false
        }
    }

    fun importImage() {
        if (!canImportImages) {
            _statusMessage.value = "Bildimport ist auf $platformName noch nicht verfuegbar."
            return
        }

        viewModelScope.launch {
            _isImportingImage.value = true
            val importedImage = runCatching {
                cameraManager.importImage()
            }.getOrElse { error ->
                _statusMessage.value = error.message ?: "Bildimport fehlgeschlagen."
                _isImportingImage.value = false
                return@launch
            }

            handleSelectedImage(
                image = importedImage,
                emptyMessage = "Keine Bilddatei ausgewaehlt.",
                successPrefix = "Bild bereit"
            )
            _isImportingImage.value = false
        }
    }

    fun analyzeScene() {
        val base64Image = selectedImageBase64
        if (base64Image.isNullOrBlank()) {
            _statusMessage.value = "Bitte zuerst ein Bild aufnehmen oder importieren."
            return
        }

        viewModelScope.launch {
            _isAnalyzingScene.value = true
            cloudVisionRepository.detectScene(base64Image)
                .onSuccess { detection ->
                    _primaryLabel.value = detection.primaryLabel
                    _detectedCandidates.value = detection.candidates
                    _statusMessage.value = "Objekterkennung abgeschlossen."
                }
                .onFailure { error ->
                    _statusMessage.value = error.message ?: "Objekterkennung fehlgeschlagen."
                }
            _isAnalyzingScene.value = false
        }
    }

    private fun handleSelectedImage(
        image: com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.ImportedImageAsset?,
        emptyMessage: String,
        successPrefix: String
    ) {
        _statusMessage.value = if (image == null) {
            emptyMessage
        } else {
            _selectedImageName.value = image.fileName
            _selectedImagePath.value = image.absolutePath
            selectedImageBase64 = image.base64Content
            _primaryLabel.value = null
            _detectedCandidates.value = emptyList()
            "$successPrefix: ${image.fileName}. Du kannst jetzt die Objekterkennung starten."
        }
    }
}

class SharedCaptureViewModel(
    private val cameraManager: CameraManager,
    private val captureGateway: CaptureGateway,
    private val cloudVisionRepository: CloudVisionRepository
) : ViewModel() {
    val platformName: String = cameraManager.platformName
    val canCaptureImages: Boolean = cameraManager.supportsDirectCapture
    val canImportImages: Boolean = cameraManager.supportsImageImport
    private var selectedImageBase64: String? = null

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _translatedText = MutableStateFlow("")
    val translatedText: StateFlow<String> = _translatedText.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _selectedImageName = MutableStateFlow<String?>(null)
    val selectedImageName: StateFlow<String?> = _selectedImageName.asStateFlow()

    private val _selectedImagePath = MutableStateFlow<String?>(null)
    val selectedImagePath: StateFlow<String?> = _selectedImagePath.asStateFlow()

    private val _isCapturingImage = MutableStateFlow(false)
    val isCapturingImage: StateFlow<Boolean> = _isCapturingImage.asStateFlow()

    private val _isImportingImage = MutableStateFlow(false)
    val isImportingImage: StateFlow<Boolean> = _isImportingImage.asStateFlow()

    private val _isAnalyzingImportedImage = MutableStateFlow(false)
    val isAnalyzingImportedImage: StateFlow<Boolean> = _isAnalyzingImportedImage.asStateFlow()

    fun openCamera() {
        _statusMessage.value = cameraManager.openCamera()
    }

    fun captureImage() {
        if (!canCaptureImages) {
            _statusMessage.value = "Direkte Bildaufnahme ist auf $platformName noch nicht verfuegbar."
            return
        }

        viewModelScope.launch {
            _isCapturingImage.value = true
            val capturedImage = runCatching {
                cameraManager.captureImage()
            }.getOrElse { error ->
                _statusMessage.value = error.message ?: "Bildaufnahme fehlgeschlagen."
                _isCapturingImage.value = false
                return@launch
            }

            handleSelectedImage(
                image = capturedImage,
                emptyMessage = "Keine Aufnahme erstellt.",
                successPrefix = "Foto bereit"
            )
            _isCapturingImage.value = false
        }
    }

    fun importImage() {
        if (!canImportImages) {
            _statusMessage.value = "Bildimport ist auf $platformName noch nicht verfuegbar."
            return
        }

        viewModelScope.launch {
            _isImportingImage.value = true
            val importedImage = runCatching {
                cameraManager.importImage()
            }.getOrElse { error ->
                _statusMessage.value = error.message ?: "Bildimport fehlgeschlagen."
                _isImportingImage.value = false
                return@launch
            }

            handleSelectedImage(
                image = importedImage,
                emptyMessage = "Keine Bilddatei ausgewaehlt.",
                successPrefix = "Bild bereit"
            )
            _isImportingImage.value = false
        }
    }

    fun analyzeImportedImage() {
        val base64Image = selectedImageBase64
        if (base64Image.isNullOrBlank()) {
            _statusMessage.value = "Bitte zuerst ein Bild aufnehmen oder importieren."
            return
        }
        viewModelScope.launch {
            _isAnalyzingImportedImage.value = true
            cloudVisionRepository.extractDocumentText(base64Image)
                .onSuccess { extractedText ->
                    _recognizedText.value = extractedText
                    _translatedText.value = ""
                    _statusMessage.value = "Cloud-OCR abgeschlossen. Text wurde uebernommen."
                }
                .onFailure { error ->
                    _statusMessage.value = error.message ?: "Cloud-OCR fehlgeschlagen."
                }
            _isAnalyzingImportedImage.value = false
        }
    }

    fun updateRecognizedText(text: String) {
        _recognizedText.value = text
    }

    fun updateTranslatedText(text: String) {
        _translatedText.value = text
    }

    fun saveCapture() {
        viewModelScope.launch {
            val error = captureGateway.saveCapture(
                recognizedText = _recognizedText.value.trim(),
                translatedText = _translatedText.value.trim()
            )
            _statusMessage.value = error ?: "Capture im Verlauf gespeichert."
        }
    }

    fun clearStatus() {
        _statusMessage.value = null
    }

    private fun handleSelectedImage(
        image: com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.ImportedImageAsset?,
        emptyMessage: String,
        successPrefix: String
    ) {
        _statusMessage.value = if (image == null) {
            emptyMessage
        } else {
            _selectedImageName.value = image.fileName
            _selectedImagePath.value = image.absolutePath
            selectedImageBase64 = image.base64Content
            _recognizedText.value = ""
            _translatedText.value = ""
            "$successPrefix: ${image.fileName}. Du kannst jetzt OCR starten oder den Text manuell anpassen."
        }
    }
}
