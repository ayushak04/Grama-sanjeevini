package com.mindmatrix.gramasanjeevini.data

import java.time.LocalDate

data class Shop(
    val id: String,
    val name: String,
    val village: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val contactNo: String,
    val operatingHours: String = "8:00 AM - 9:00 PM",
)

data class VillageLocation(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

data class Medicine(
    val id: String,
    val name: String,
    val isLifeSaving: Boolean = false,
    val expiryDate: LocalDate? = null,
    val basePrice: Double,
    val requiresPrescription: Boolean,
    val isRare: Boolean,
)

val Medicine.needsPrescriptionForArrangement: Boolean
    get() = requiresPrescription && isRare

data class MedicineRequest(
    val id: String,
    val shopId: String,
    val medicineId: String?,
    val medicineName: String,
    val requestedQuantity: Int,
    val patientNote: String,
    val prescriptionDocumentUri: String?,
    val requestedOn: LocalDate,
    val villagerPhone: String = "",
    val villagerAddress: String = "",
    val hasPrescription: Boolean = prescriptionDocumentUri != null,
    val villagerRated: Boolean = false,
    val pharmacistRated: Boolean = false,
    val issueReported: Boolean = false,
    val visibleToVillager: Boolean = true,
    val visibleToPharmacist: Boolean = true,
    val pharmacistMessage: String = "",
)

data class StockItem(
    val id: String,
    val shopId: String,
    val medicineId: String,
    val quantity: Int,
    val expiryDate: LocalDate,
    val discountSale: Boolean,
)

data class MedicineSearchResult(
    val shop: Shop,
    val medicine: Medicine,
    val stock: StockItem,
    val distanceKm: Double,
) {
    val stockStatus: String
        get() = when {
            stock.quantity <= 0 -> "Out of stock"
            stock.quantity <= 5 -> "Low stock"
            else -> "Available"
        }

    val expiresSoon: Boolean
        get() = stock.expiryDate <= LocalDate.now().plusDays(30)
}

data class ShopSearchResult(
    val shop: Shop,
    val distanceKm: Double,
)
