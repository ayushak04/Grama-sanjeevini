package com.mindmatrix.gramasanjeevini.auth.domain

sealed interface AuthResult {
    data class Success(val profile: UserProfile) : AuthResult
    data class Failure(val message: String) : AuthResult
}
