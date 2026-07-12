package com.mindmatrix.gramasanjeevini.data

import com.google.firebase.Timestamp
import java.util.Locale

data class PharmacistProfile(
    val uid: String = "",
    val email: String = "",
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
    val isApproved: Boolean = false,
    val role: String = "pharmacist",
    val ratingSum: Double = 0.0,
    val ratingCount: Int = 0,
)

data class ShopResult(
    val uid: String = "",
    val shopName: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val ratingSum: Double = 0.0,
    val ratingCount: Int = 0,
)

enum class ArrangementStatus {
    Pending,
    Accepted,
    Completed,
    Rejected,
}

data class ArrangementRequest(
    val requestId: String = "",
    val villagerId: String? = null,
    val villagerName: String = "",
    val villagerPhone: String = "",
    val villagerAddress: String = "",
    val villagerLatitude: Double? = null,
    val villagerLongitude: Double? = null,
    val villagerRatingSum: Double = 0.0,
    val villagerRatingCount: Int = 0,
    val medicineName: String = "",
    val quantity: Int = 1,
    val hasPrescription: Boolean = false,
    val status: ArrangementStatus = ArrangementStatus.Pending,
    val shopId: String? = null,
    val shopName: String = "",
    val prescriptionDocumentUri: String? = null,
    val prescriptionUrl: String? = null,
    val villagerRated: Boolean = false,
    val pharmacistRated: Boolean = false,
    val issueReported: Boolean = false,
    val visibleToVillager: Boolean = true,
    val visibleToPharmacist: Boolean = true,
    val pharmacistMessage: String = "",
    val createdAtMillis: Long = 0L,
)

fun ratingDisplayText(ratingSum: Double, ratingCount: Int): String {
    if (ratingCount <= 0) return "No ratings yet"
    val average = (ratingSum / ratingCount.toDouble()).coerceIn(0.0, 5.0)
    return "⭐ ${String.format(Locale.US, "%.1f", average)}/5 ($ratingCount)"
}

data class MedicineItem(
    val id: String = "",
    val medicineName: String = "",
    val isLifeSaving: Boolean = false,
    val shopName: String = "",
    val shopArea: String = "",
    val shopAddress: String = "",
    val fullAddress: String = "",
    val distanceKm: Double? = null,
    val stockStatus: String = "Available",
    val shopId: String = "",
    val requiresPrescription: Boolean = false,
    val expiryDate: Timestamp? = null,
    val quantity: Int = 0,
    val shopPhone: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) {
    val medicineId: String
        get() = id

    val name: String
        get() = medicineName
}
