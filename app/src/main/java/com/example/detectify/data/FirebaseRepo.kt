package com.example.detectify.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object FirebaseRepo {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val storageRef by lazy { FirebaseStorage.getInstance().reference }

    /**
     * Try sign in. If sign in fails because account doesn't exist, create account.
     * Returns Result.success(Unit) on success or Result.failure(exception) on failure.
     */
    suspend fun signInOrCreate(email: String, password: String): Result<Unit> {
        return try {
            // Try sign in
            val signInTask = auth.signInWithEmailAndPassword(email, password).await()
            if (auth.currentUser != null) {
                Result.success(Unit)
            } else {
                // Fallback: create user (shouldn't normally get here)
                auth.createUserWithEmailAndPassword(email, password).await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            // If sign in fails because user not found, try create user
            return try {
                auth.createUserWithEmailAndPassword(email, password).await()
                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun currentUserId(): String? = auth.currentUser?.uid

    /**
     * Upload profile image given a content Uri (from gallery/picker).
     * Returns Result.success(downloadUrl) or Result.failure(exception).
     */
    suspend fun uploadProfileImage(localUri: Uri): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not signed in"))
        return try {
            val ref = storageRef.child("profiles/$uid.jpg")
            // Upload
            ref.putFile(localUri).await()
            // Get download URL
            val url = ref.downloadUrl.await().toString()
            // Update FirebaseAuth user profile photoURL
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build()
            auth.currentUser?.updateProfile(profileUpdate)?.await()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
