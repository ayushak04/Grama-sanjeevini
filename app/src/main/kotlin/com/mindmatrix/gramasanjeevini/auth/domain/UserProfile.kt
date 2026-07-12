package com.mindmatrix.gramasanjeevini.auth.domain

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: UserRole = UserRole.Villager,
    val shopName: String = "",
    val ownerName: String = "",
    val drugLicenseNumber: String = "",
    val pharmacistRegNumber: String = "",
    val gstin: String = "",
    val shopTimings: String = "",
    val hasFssai: Boolean = false,
    val fssaiNumber: String = "",
    val phone: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val age: Int = 0,
    val ratingSum: Double = 0.0,
    val ratingCount: Int = 0,
    val isApproved: Boolean = false,
)

data class PharmacistSignupForm(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val shopName: String,
    val ownerName: String,
    val drugLicenseNumber: String,
    val pharmacistRegNumber: String,
    val phone: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

data class VillagerSignupForm(
    val fullName: String,
    val phone: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
)
