package com.mindmatrix.gramasanjeevini.auth.domain

enum class UserRole(val wireName: String) {
    Villager("villager"),
    Pharmacist("pharmacist"),
    Admin("admin");

    companion object {
        fun fromWireName(value: String?): UserRole? =
            entries.firstOrNull { it.wireName.equals(value?.trim(), ignoreCase = true) }
    }
}
