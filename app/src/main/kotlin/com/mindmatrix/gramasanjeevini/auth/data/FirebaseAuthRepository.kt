package com.mindmatrix.gramasanjeevini.auth.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mindmatrix.gramasanjeevini.auth.domain.AuthResult
import com.mindmatrix.gramasanjeevini.auth.domain.PharmacistSignupForm
import com.mindmatrix.gramasanjeevini.auth.domain.UserProfile
import com.mindmatrix.gramasanjeevini.auth.domain.UserRole
import com.mindmatrix.gramasanjeevini.auth.domain.VillagerSignupForm
import com.mindmatrix.gramasanjeevini.data.PharmacistProfile
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {
    private val usersCollection = firestore.collection("users")

    override suspend fun currentUserProfile(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        return getProfile(uid)
    }

    override suspend fun signupVillager(form: VillagerSignupForm): AuthResult = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(form.email.trim(), form.password).await()
        val firebaseUser = requireNotNull(authResult.user) { "Firebase did not return a user." }
        val uid = firebaseUser.uid
        val profile = UserProfile(
            uid = uid,
            fullName = form.fullName.trim(),
            email = form.email.trim(),
            role = UserRole.Villager,
            phone = form.phone.trim(),
            age = 0,
            address = form.address.trim(),
            latitude = form.latitude,
            longitude = form.longitude,
            ratingSum = 0.0,
            ratingCount = 0,
            isApproved = true,
        )

        withTimeout(10000) {
            usersCollection.document(uid).set(profile.toFirestoreMap(), SetOptions.merge()).await()
        }
        firebaseUser.sendEmailVerification().await()

        profile
    }.also {
        auth.signOut()
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Failure(it.readableMessage()) },
    )

    override suspend fun signupPharmacist(form: PharmacistSignupForm): AuthResult = runCatching {
        // 1. Create Auth User
        val authResult = auth.createUserWithEmailAndPassword(form.email.trim(), form.password).await()
        val firebaseUser = requireNotNull(authResult.user) { "Firebase did not return a user." }
        val uid = firebaseUser.uid
        
        val pharmacistProfile = PharmacistProfile(
            uid = uid,
            email = form.email.trim(),
            shopName = form.shopName.trim(),
            ownerName = form.ownerName.trim(),
            drugLicenseNumber = form.drugLicenseNumber.trim(),
            pharmacistRegNumber = form.pharmacistRegNumber.trim(),
            phone = form.phone.trim(),
            address = form.address.trim(),
            latitude = form.latitude,
            longitude = form.longitude,
            isApproved = false,
        )
        val profile = UserProfile(
            uid = uid,
            fullName = pharmacistProfile.ownerName,
            email = pharmacistProfile.email,
            role = UserRole.Pharmacist,
            shopName = pharmacistProfile.shopName,
            ownerName = pharmacistProfile.ownerName,
            drugLicenseNumber = pharmacistProfile.drugLicenseNumber,
            pharmacistRegNumber = pharmacistProfile.pharmacistRegNumber,
            gstin = pharmacistProfile.gstin,
            shopTimings = pharmacistProfile.shopTimings,
            hasFssai = pharmacistProfile.hasFssai,
            fssaiNumber = pharmacistProfile.fssaiNumber,
            phone = pharmacistProfile.phone,
            address = pharmacistProfile.address,
            latitude = pharmacistProfile.latitude,
            longitude = pharmacistProfile.longitude,
            age = 0,
            ratingSum = 0.0,
            ratingCount = 0,
            isApproved = pharmacistProfile.isApproved,
        )

        // 2. Save to Firestore with a 10-second timeout to prevent UI hang
        try {
            withTimeout(10000) {
                usersCollection.document(uid).set(
                    pharmacistProfile.toFirestoreMap(role = UserRole.Pharmacist),
                    SetOptions.merge(),
                ).await()
            }
        } catch (e: Exception) {
            // Optional: If Firestore fails, you might want to sign out or delete the auth user
            // to allow the user to try again properly.
            throw Exception("Account created, but profile failed to save: ${e.localizedMessage}")
        }
        firebaseUser.sendEmailVerification().await()
        
        profile
    }.also {
        auth.signOut()
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Failure(it.readableMessage()) },
    )

    override suspend fun login(
        email: String,
        password: String,
        expectedRole: UserRole,
    ): AuthResult = runCatching {
        val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val uid = requireNotNull(authResult.user?.uid) { "Firebase did not return a user id." }
        auth.currentUser?.reload()?.await()
        val profile = getProfile(uid) ?: error("No profile found in Firestore users/$uid.")

        if (profile.role != expectedRole) {
            auth.signOut()
            error("This account is registered as ${profile.role.wireName}, not ${expectedRole.wireName}.")
        }

        if (profile.role != UserRole.Admin && auth.currentUser?.isEmailVerified != true) {
            auth.signOut()
            error(UNVERIFIED_EMAIL_MESSAGE)
        }

        profile
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Failure(it.readableMessage()) },
    )

    override suspend fun resendVerificationEmail(
        email: String,
        password: String,
        expectedRole: UserRole,
    ): AuthResult = runCatching {
        val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val uid = requireNotNull(authResult.user?.uid) { "Firebase did not return a user id." }
        val profile = getProfile(uid) ?: error("No profile found in Firestore users/$uid.")

        if (profile.role != expectedRole) {
            auth.signOut()
            error("This account is registered as ${profile.role.wireName}, not ${expectedRole.wireName}.")
        }

        val currentUser = auth.currentUser ?: error("Unable to reload the signed-in user.")
        currentUser.reload().await()
        if (currentUser.isEmailVerified) {
            auth.signOut()
            return@runCatching profile
        }

        currentUser.sendEmailVerification().await()
        auth.signOut()
        profile
    }.also {
        auth.signOut()
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Failure(it.readableMessage()) },
    )

    override suspend fun sendPasswordReset(email: String): AuthResult = runCatching {
        auth.sendPasswordResetEmail(email.trim()).await()
        UserProfile(email = email.trim())
    }.fold(
        onSuccess = { AuthResult.Success(it) },
        onFailure = { AuthResult.Failure(it.readableMessage()) },
    )

    override fun logout() {
        auth.signOut()
    }

    private suspend fun getProfile(uid: String): UserProfile? {
        // Adding timeout to profile fetch as well
        val document = withTimeout(10000) {
            usersCollection.document(uid).get().await()
        }
        if (!document.exists()) return null

        return UserProfile(
            uid = document.getString("uid") ?: document.id,
            fullName = document.getString("ownerName") ?: document.getString("fullName").orEmpty(),
            email = document.getString("email").orEmpty(),
            role = UserRole.fromWireName(document.getString("role")) ?: UserRole.Villager,
            shopName = document.getString("shopName").orEmpty(),
            ownerName = document.getString("ownerName").orEmpty(),
            drugLicenseNumber = document.getString("drugLicenseNumber").orEmpty(),
            pharmacistRegNumber = document.getString("pharmacistRegNumber").orEmpty(),
            gstin = document.getString("gstin").orEmpty(),
            shopTimings = document.getString("shopTimings").orEmpty(),
            hasFssai = document.getBoolean("hasFssai") ?: false,
            fssaiNumber = document.getString("fssaiNumber").orEmpty(),
            phone = document.getString("phone") ?: document.getString("contactNo").orEmpty(),
            address = document.getString("address").orEmpty(),
            latitude = (document.get("latitude") as? Number)?.toDouble(),
            longitude = (document.get("longitude") as? Number)?.toDouble(),
            age = (document.get("age") as? Number)?.toInt() ?: 0,
            ratingSum = (document.get("ratingSum") as? Number)?.toDouble() ?: 0.0,
            ratingCount = (document.get("ratingCount") as? Number)?.toInt() ?: 0,
            isApproved = document.getBoolean("isApproved") ?: false,
        )
    }

    private fun PharmacistProfile.toFirestoreMap(role: UserRole): Map<String, Any> = mapOf(
        "uid" to uid,
        "email" to email,
        "role" to this.role.ifBlank { role.wireName },
        "shopName" to shopName,
        "ownerName" to ownerName,
        "drugLicenseNumber" to drugLicenseNumber,
        "pharmacistRegNumber" to pharmacistRegNumber,
        "gstin" to gstin,
        "shopTimings" to shopTimings,
        "hasFssai" to hasFssai,
        "fssaiNumber" to if (hasFssai) fssaiNumber else "",
        "phone" to phone,
        "address" to address,
        "shopAddress" to address,
        "fullAddress" to address,
        "latitude" to (latitude ?: 0.0),
        "longitude" to (longitude ?: 0.0),
        "isApproved" to isApproved,
        "ratingSum" to 0.0,
        "ratingCount" to 0,
        "createdAt" to Timestamp.now(),
    )

    private fun UserProfile.toFirestoreMap(): Map<String, Any> = mapOf(
        "uid" to uid,
        "fullName" to fullName,
        "email" to email,
        "role" to role.wireName,
        "phone" to phone,
        "address" to address,
        "latitude" to (latitude ?: 0.0),
        "longitude" to (longitude ?: 0.0),
        "age" to age,
        "ratingSum" to ratingSum,
        "ratingCount" to ratingCount,
        "isApproved" to isApproved,
        "createdAt" to Timestamp.now(),
    )

    private fun Throwable.readableMessage(): String = when (this) {
        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password. Please try again."
        is FirebaseAuthInvalidUserException -> "Account not found. Please sign up."
        is FirebaseAuthUserCollisionException -> "An account with this email already exists. Please log in."
        is FirebaseAuthException -> when (errorCode) {
            "ERROR_USER_NOT_FOUND" -> "Account not found. Please sign up."
            "ERROR_WRONG_PASSWORD",
            "ERROR_INVALID_CREDENTIAL",
            "ERROR_INVALID_EMAIL",
            -> "Invalid email or password. Please try again."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists. Please log in."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please wait a moment and try again."
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection and try again."
            else -> "Authentication failed. Please try again."
        }
        else -> message?.takeIf { it.isNotBlank() } ?: "Something went wrong. Please try again."
    }

    private companion object {
        const val UNVERIFIED_EMAIL_MESSAGE =
            "Your email is not verified. Please check your inbox and verify your email to continue."
    }
}
