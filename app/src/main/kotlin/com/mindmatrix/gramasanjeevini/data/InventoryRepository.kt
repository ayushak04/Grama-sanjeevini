package com.mindmatrix.gramasanjeevini.data

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Legacy facade kept so older screens compile while dashboards migrate to
 * Firestore-backed ViewModels. Do not add mock data here.
 */
@Singleton
class InventoryRepository @Inject constructor() {
    fun villageLocations(): List<VillageLocation> = emptyList()

    fun searchMedicines(query: String, radiusKm: Int, originVillageId: String): List<MedicineSearchResult> =
        emptyList()

    fun emergencyMedicines(radiusKm: Int, originVillageId: String): List<MedicineSearchResult> =
        emptyList()

    fun searchNearbyShops(query: String, radiusKm: Int, originVillageId: String): List<ShopSearchResult> =
        emptyList()

    fun allStockForPharmacist(shopId: String = ""): List<MedicineSearchResult> =
        emptyList()

    fun updateStock(stockId: String, delta: Int) = Unit

    fun submitMedicineRequest(
        result: MedicineSearchResult,
        requestedQuantity: Int,
        patientNote: String,
        prescriptionDocumentUri: String?,
    ): ArrangementRequest = ArrangementRequest(
        requestId = "",
        villagerName = patientNote,
        villagerPhone = "",
        medicineName = result.medicine.name,
        quantity = requestedQuantity,
        hasPrescription = prescriptionDocumentUri != null,
        status = ArrangementStatus.Pending,
        shopId = result.shop.id,
        prescriptionDocumentUri = prescriptionDocumentUri,
    )

    fun submitShopArrangementRequest(
        shop: Shop,
        villagerName: String,
        villagerPhone: String,
        medicineName: String,
        requestedQuantity: Int,
        prescriptionDocumentUri: String?,
    ): ArrangementRequest = ArrangementRequest(
        requestId = "",
        villagerName = villagerName,
        villagerPhone = villagerPhone,
        medicineName = medicineName,
        quantity = requestedQuantity,
        hasPrescription = prescriptionDocumentUri != null,
        status = ArrangementStatus.Pending,
        shopId = shop.id,
        prescriptionDocumentUri = prescriptionDocumentUri,
    )
}
