package com.mindmatrix.gramasanjeevini.core

import com.mindmatrix.gramasanjeevini.auth.domain.PharmacistSignupForm
import com.mindmatrix.gramasanjeevini.auth.domain.VillagerSignupForm

object Validation {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private val phoneRegex = Regex("^[0-9]{10}$")

    fun email(email: String): String? = when {
        email.isBlank() -> "Email is required."
        !emailRegex.matches(email.trim()) -> "Enter a valid email address."
        else -> null
    }

    fun password(password: String): String? = when {
        password.isBlank() -> "Password is required."
        password.length < 6 -> "Password must be at least 6 characters."
        else -> null
    }

    fun login(email: String, password: String): String? =
        email(email) ?: password(password)

    fun forgotPassword(email: String): String? = email(email)

    fun villagerSignup(form: VillagerSignupForm): String? = when {
        form.fullName.trim().length < 3 -> "Enter your full name."
        !phoneRegex.matches(form.phone.trim()) -> "Enter a 10 digit phone number."
        email(form.email) != null -> email(form.email)
        password(form.password) != null -> password(form.password)
        form.password != form.confirmPassword -> "Passwords do not match."
        form.address.trim().length < 6 -> "Enter your full address or nearest landmark."
        else -> null
    }

    fun pharmacistSignup(form: PharmacistSignupForm): String? = when {
        email(form.email) != null -> email(form.email)
        password(form.password) != null -> password(form.password)
        form.password != form.confirmPassword -> "Passwords do not match."
        form.shopName.trim().length < 3 -> "Enter the medical shop name."
        form.ownerName.trim().length < 3 -> "Enter the owner's full name."
        form.drugLicenseNumber.trim().isBlank() -> "Drug license number is required."
        form.pharmacistRegNumber.trim().isBlank() -> "Registered pharmacist number is required."
        !phoneRegex.matches(form.phone.trim()) -> "Enter a 10 digit phone number."
        form.address.trim().length < 10 -> "Enter the complete pharmacy address."
        else -> null
    }
}
