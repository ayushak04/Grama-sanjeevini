package com.mindmatrix.gramasanjeevini.auth.data

import com.mindmatrix.gramasanjeevini.auth.domain.AuthResult
import com.mindmatrix.gramasanjeevini.auth.domain.PharmacistSignupForm
import com.mindmatrix.gramasanjeevini.auth.domain.UserProfile
import com.mindmatrix.gramasanjeevini.auth.domain.UserRole
import com.mindmatrix.gramasanjeevini.auth.domain.VillagerSignupForm

interface AuthRepository {
    suspend fun currentUserProfile(): UserProfile?
    suspend fun signupVillager(form: VillagerSignupForm): AuthResult
    suspend fun signupPharmacist(form: PharmacistSignupForm): AuthResult
    suspend fun login(email: String, password: String, expectedRole: UserRole): AuthResult
    suspend fun resendVerificationEmail(email: String, password: String, expectedRole: UserRole): AuthResult
    suspend fun sendPasswordReset(email: String): AuthResult
    fun logout()
}
