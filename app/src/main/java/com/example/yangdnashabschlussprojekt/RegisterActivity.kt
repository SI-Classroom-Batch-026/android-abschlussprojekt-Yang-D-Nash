package com.example.yangdnashabschlussprojekt

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private val userRepository = UserRepository(FirebaseAuth.getInstance())

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            registerUser(selectedImageUri)
        }
    }

    private fun openMediaPicker() {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    fun onSelectImageClicked(view: View) {
        openMediaPicker()
    }

    private fun registerUser(profileImageUri: Uri?) {
        val email = "test@example.com"
        val password = "123456"
        val displayName = "Max Mustermann"

        userRepository.registerUser(
            email = email,
            password = password,
            displayName = displayName,
            profileImageUri = profileImageUri
        ) { success, error ->
            if (success) {
                Toast.makeText(this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fehler: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
