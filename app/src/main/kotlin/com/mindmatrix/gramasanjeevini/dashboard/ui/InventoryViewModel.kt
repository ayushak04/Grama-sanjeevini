package com.mindmatrix.gramasanjeevini.dashboard.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mindmatrix.gramasanjeevini.auth.data.AuthRepository
import com.mindmatrix.gramasanjeevini.data.InventoryRepository
import com.mindmatrix.gramasanjeevini.data.MedicineSearchResult
import com.mindmatrix.gramasanjeevini.data.ShopSearchResult
import com.mindmatrix.gramasanjeevini.data.VillageLocation
import com.mindmatrix.gramasanjeevini.data.needsPrescriptionForArrangement
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    var searchQuery by mutableStateOf("Insulin")
        private set

    var shopSearchQuery by mutableStateOf("")
        private set

    var radiusKm by mutableIntStateOf(15)
        private set

    var selectedVillageId by mutableStateOf("ravugodlu")
        private set

    var selectedSection by mutableStateOf(DashboardSection.Home)
        private set

    var selectedSearchTab by mutableStateOf(SearchRequestTab.FindMedicines)
        private set

    val villageLocations: List<VillageLocation> = repository.villageLocations()

    var searchResults by mutableStateOf<List<MedicineSearchResult>>(emptyList())
        private set

    var emergencyResults by mutableStateOf<List<MedicineSearchResult>>(emptyList())
        private set

    var shopResults by mutableStateOf<List<ShopSearchResult>>(emptyList())
        private set

    var pharmacistStock by mutableStateOf<List<MedicineSearchResult>>(emptyList())
        private set

    var requestStatusMessage by mutableStateOf<String?>(null)
        private set

    init {
        refresh()
    }

    fun onQueryChanged(query: String) {
        searchQuery = query
        refresh()
    }

    fun onShopQueryChanged(query: String) {
        shopSearchQuery = query
        refresh()
    }

    fun onRadiusChanged(radius: Int) {
        radiusKm = radius
        refresh()
    }

    fun onVillageChanged(villageId: String) {
        selectedVillageId = villageId
        refresh()
    }

    fun showSection(section: DashboardSection) {
        selectedSection = section
    }

    fun showSearchTab(tab: SearchRequestTab) {
        selectedSearchTab = tab
    }

    fun adjustStock(stockId: String, delta: Int) {
        repository.updateStock(stockId, delta)
        refresh()
    }

    fun submitArrangementRequest(
        result: MedicineSearchResult,
        requestedQuantity: Int,
        patientNote: String,
        prescriptionDocumentUri: String?,
    ): Boolean {
        if (result.medicine.needsPrescriptionForArrangement && prescriptionDocumentUri == null) {
            requestStatusMessage = "Upload a doctor prescription document before requesting this rare medicine."
            return false
        }

        repository.submitMedicineRequest(
            result = result,
            requestedQuantity = requestedQuantity,
            patientNote = patientNote,
            prescriptionDocumentUri = prescriptionDocumentUri,
        )
        requestStatusMessage = "Request sent to ${result.shop.name} for ${result.medicine.name}."
        return true
    }

    fun submitShopArrangementRequest(
        result: ShopSearchResult,
        villagerName: String,
        villagerPhone: String,
        medicineName: String,
        requestedQuantity: Int,
        prescriptionDocumentUri: String?,
    ): Boolean {
        val trimmedVillagerName = villagerName.trim()
        val trimmedVillagerPhone = villagerPhone.trim()
        val trimmedMedicineName = medicineName.trim()
        if (trimmedVillagerName.length < 2) {
            requestStatusMessage = "Enter the patient or villager name before submitting."
            return false
        }

        if (!Regex("^[0-9]{10}$").matches(trimmedVillagerPhone)) {
            requestStatusMessage = "Enter a 10 digit contact number before submitting."
            return false
        }

        if (trimmedMedicineName.isBlank()) {
            requestStatusMessage = "Enter a medicine name before submitting the arrangement request."
            return false
        }

        if (prescriptionDocumentUri == null) {
            requestStatusMessage = "Upload a doctor prescription before submitting the arrangement request."
            return false
        }

        repository.submitShopArrangementRequest(
            shop = result.shop,
            villagerName = trimmedVillagerName,
            villagerPhone = trimmedVillagerPhone,
            medicineName = trimmedMedicineName,
            requestedQuantity = requestedQuantity,
            prescriptionDocumentUri = prescriptionDocumentUri,
        )
        requestStatusMessage = "Arrangement request sent to ${result.shop.name} for $trimmedMedicineName. They can call $trimmedVillagerPhone."
        return true
    }

    fun clearRequestStatus() {
        requestStatusMessage = null
    }

    fun logout() {
        authRepository.logout()
    }

    private fun refresh() {
        searchResults = repository.searchMedicines(searchQuery, radiusKm, selectedVillageId)
        emergencyResults = repository.emergencyMedicines(radiusKm, selectedVillageId)
        shopResults = repository.searchNearbyShops(shopSearchQuery, radiusKm, selectedVillageId)
        pharmacistStock = repository.allStockForPharmacist()
    }
}

enum class SearchRequestTab {
    Profile,
    FindMedicines,
    DirectShopRequest,
    RequestLogs,
}

enum class DashboardSection {
    Home,
    Search,
    Emergency,
    Stock,
}
