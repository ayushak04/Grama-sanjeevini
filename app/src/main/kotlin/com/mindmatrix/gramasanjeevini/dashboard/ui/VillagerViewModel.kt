package com.mindmatrix.gramasanjeevini.dashboard.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.mindmatrix.gramasanjeevini.auth.domain.UserProfile
import com.mindmatrix.gramasanjeevini.auth.domain.UserRole
import com.mindmatrix.gramasanjeevini.data.ArrangementRequest
import com.mindmatrix.gramasanjeevini.data.ArrangementStatus
import com.mindmatrix.gramasanjeevini.data.MedicineItem
import com.mindmatrix.gramasanjeevini.data.ShopResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

sealed class MedicineUiState {
    object Loading : MedicineUiState()
    data class Success(val medicines: List<MedicineItem>) : MedicineUiState()
    data class Error(val message: String) : MedicineUiState()
}

data class VillagerLocationUiState(
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    val hasCoordinates: Boolean
        get() = latitude != null && longitude != null
}

private data class PharmacyLocationSnapshot(
    val shopName: String = "",
    val phone: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@HiltViewModel
class VillagerViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ViewModel() {
    private companion object {
        const val TAG = "VillagerViewModel"
        const val MEDICINES_COLLECTION = "medicines"
        const val USERS_COLLECTION = "users"
        const val REQUESTS_COLLECTION = "requests"
    }

    private var medicinesRegistration: ListenerRegistration? = null

    private val _isGuest = MutableStateFlow(auth.currentUser == null)
    val isGuest: StateFlow<Boolean> = _isGuest

    private val _medicineQuery = MutableStateFlow("")
    val medicineQuery: StateFlow<String> = _medicineQuery

    private val _selectedLocation = MutableStateFlow("")
    val selectedLocation: StateFlow<String> = _selectedLocation

    private val _selectedDistanceKm = MutableStateFlow(5)
    val selectedDistanceKm: StateFlow<Int> = _selectedDistanceKm

    val locationFilters: List<String> = listOf("Ravugodlu", "Irulegere", "Kanakapura", "Harohalli")
    val distanceFilters: List<Int> = listOf(5, 10, 15, 25)

    private val _villagerLocation = MutableStateFlow(VillagerLocationUiState())
    val villagerLocation: StateFlow<VillagerLocationUiState> = _villagerLocation

    val villagerProfile: StateFlow<UserProfile?> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }

        val registration = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Unable to listen to villager profile", error)
                    trySend(null)
                    return@addSnapshotListener
                }
                val profile = snapshot?.toUserProfile()
                if (profile?.latitude != null && profile.longitude != null) {
                    _villagerLocation.value = VillagerLocationUiState(
                        address = profile.address,
                        latitude = profile.latitude,
                        longitude = profile.longitude,
                    )
                }
                trySend(profile)
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val rawMedicineState = MutableStateFlow<MedicineUiState>(MedicineUiState.Loading)

    private val pharmacyLocationSnapshots: StateFlow<Map<String, PharmacyLocationSnapshot>> = callbackFlow {
        val registration = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("role", "pharmacist")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Unable to listen to pharmacist locations", error)
                    trySend(emptyMap())
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty().associate { document ->
                        val shopId = document.getString("uid") ?: document.id
                        shopId to PharmacyLocationSnapshot(
                            shopName = document.getString("shopName").orEmpty(),
                            phone = document.getString("shopPhone")
                                ?: document.getString("phone")
                                ?: "",
                            address = document.getString("shopAddress")
                                ?: document.getString("fullAddress")
                                ?: document.getString("address")
                                ?: "",
                            latitude = document.numberAsDouble("latitude"),
                            longitude = document.numberAsDouble("longitude"),
                        )
                    },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val medicineState: StateFlow<MedicineUiState> = combine(
        rawMedicineState,
        villagerLocation,
        pharmacyLocationSnapshots,
    ) { state, location, pharmacyLocations ->
        when (state) {
            MedicineUiState.Loading -> MedicineUiState.Loading
            is MedicineUiState.Error -> state
            is MedicineUiState.Success -> MedicineUiState.Success(
                state.medicines.map { medicine ->
                    val syncedMedicine = medicine.withPharmacyLocationFallback(pharmacyLocations[medicine.shopId])
                    syncedMedicine.copy(distanceKm = syncedMedicine.distanceFrom(location))
                },
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MedicineUiState.Loading)

    init {
        observeMedicines()
    }

    private fun observeMedicines() {
        medicinesRegistration?.remove()
        medicinesRegistration = firestore.collection(MEDICINES_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Unable to listen to $MEDICINES_COLLECTION collection", error)
                    rawMedicineState.value = MedicineUiState.Error(error.message ?: "Unknown Error")
                    return@addSnapshotListener
                }

                val medicines = snapshot?.documents.orEmpty()
                    .filter { document ->
                        document.getBoolean("isRemovedByAdmin") != true &&
                            document.getBoolean("isDeletedByPharmacist") != true &&
                            document.getBoolean("visibleToVillager") != false &&
                            document.getString("status") != "removed"
                    }
                    .mapNotNull { document ->
                    try {
                        MedicineItem(
                            id = document.id,
                            medicineName = document.getString("medicineName")
                                ?: document.getString("name")
                                ?: "",
                            shopId = document.getString("shopId") ?: document.getString("pharmacistId").orEmpty(),
                            shopName = document.getString("shopName") ?: "Unknown Shop",
                            shopArea = document.getString("shopArea")
                                ?: document.getString("area")
                                ?: document.getString("village")
                                ?: "",
                            shopAddress = document.getString("fullAddress")
                                ?: document.getString("shopAddress")
                                ?: "",
                            fullAddress = document.getString("fullAddress")
                                ?: document.getString("shopAddress")
                                ?: "",
                            stockStatus = document.getString("stockStatus") ?: "Available",
                            quantity = document.getLong("quantity")?.toInt() ?: 0,
                            requiresPrescription = document.getBoolean("requiresPrescription") ?: false,
                            expiryDate = document.expiryTimestamp(),
                            isLifeSaving = document.getBoolean("lifeSaving")
                                ?: document.getBoolean("isLifeSaving")
                                ?: false,
                            distanceKm = null,
                            shopPhone = document.getString("shopPhone") ?: document.getString("phone") ?: "",
                            latitude = document.numberAsDouble("latitude") ?: 0.0,
                            longitude = document.numberAsDouble("longitude") ?: 0.0,
                        )
                    } catch (exception: Exception) {
                        Log.e(
                            TAG,
                            "Failed to parse medicine document ${document.id}: ${exception.message}",
                            exception,
                        )
                        null
                    }
                }

                rawMedicineState.value = MedicineUiState.Success(medicines)
            }
    }

    val medicines: StateFlow<List<MedicineItem>> = combine(
        medicineState,
        medicineQuery,
        selectedLocation,
        selectedDistanceKm,
        villagerLocation,
    ) { state, query, location, distanceKm, villagerLocation ->
        val medicines = (state as? MedicineUiState.Success)?.medicines.orEmpty()
        medicines.filter { medicine ->
            val matchesQuery = query.isBlank() ||
                medicine.medicineName.contains(query, ignoreCase = true)
            val matchesLocation = location.isBlank() ||
                medicine.shopArea.equals(location, ignoreCase = true)
            val matchesDistance = if (villagerLocation.hasCoordinates) {
                medicine.distanceKm?.let { it <= distanceKm } == true
            } else {
                true
            }

            matchesQuery && matchesLocation && matchesDistance
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val emergencyMedicines: StateFlow<List<MedicineItem>> = medicineState
        .map { state ->
            (state as? MedicineUiState.Success)
                ?.medicines
                .orEmpty()
                .filter { it.isLifeSaving || it.requiresPrescription }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val approvedShops: StateFlow<List<ShopResult>> = callbackFlow {
        val registration = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("role", "pharmacist")
            .whereEqualTo("isApproved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Unable to listen to approved pharmacies", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty()
                        .filter { document ->
                            document.getBoolean("isRemovedByAdmin") != true &&
                                document.getString("status") != "removed"
                        }
                        .map { document ->
                            ShopResult(
                                uid = document.getString("uid") ?: document.id,
                                shopName = document.getString("shopName").orEmpty(),
                                ownerName = document.getString("ownerName").orEmpty(),
                                phone = document.getString("phone").orEmpty(),
                                address = document.getString("shopAddress")
                                    ?: document.getString("fullAddress")
                                    ?: document.getString("address")
                                    ?: "",
                                latitude = document.numberAsDouble("latitude"),
                                longitude = document.numberAsDouble("longitude"),
                                ratingSum = document.numberAsDouble("ratingSum") ?: 0.0,
                                ratingCount = (document.get("ratingCount") as? Number)?.toInt() ?: 0,
                            )
                        },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val requestLogs: StateFlow<List<ArrangementRequest>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val registration = firestore.collection(REQUESTS_COLLECTION)
            .whereEqualTo("villagerId", userId)
            .whereEqualTo("visibleToVillager", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Unable to listen to villager request logs", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(
                    snapshot?.documents.orEmpty()
                        .map { document -> document.toArrangementRequest() }
                        .sortedByDescending { request -> request.createdAtMillis },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onMedicineQueryChanged(query: String) {
        _medicineQuery.value = query.take(80)
    }

    fun onLocationSelected(location: String) {
        _selectedLocation.value = location
    }

    fun onDistanceSelected(distanceKm: Int) {
        _selectedDistanceKm.value = distanceKm
    }

    fun updateVillagerManualLocation(
        address: String,
        latitude: Double?,
        longitude: Double?,
    ) {
        _villagerLocation.value = VillagerLocationUiState(
            address = address.trim(),
            latitude = latitude,
            longitude = longitude,
        )
    }

    fun updateVillagerLiveLocation(latitude: Double, longitude: Double) {
        _villagerLocation.value = VillagerLocationUiState(
            address = "Live location",
            latitude = latitude,
            longitude = longitude,
        )
    }

    fun clearVillagerLocation() {
        _villagerLocation.value = VillagerLocationUiState()
    }

    fun updateVillagerProfile(
        fullName: String,
        age: Int,
        address: String,
        latitude: Double? = null,
        longitude: Double? = null,
    ) {
        val userId = auth.currentUser?.uid ?: return
        val fields = mutableMapOf<String, Any>(
            "fullName" to fullName.trim(),
            "age" to age.coerceIn(0, 120),
            "address" to address.trim(),
        )
        if (latitude != null && longitude != null) {
            fields["latitude"] = latitude
            fields["longitude"] = longitude
        }

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(fields, SetOptions.merge())
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to update villager profile for userId=$userId", error)
            }
    }

    fun submitRequest(
        villagerName: String,
        villagerPhone: String,
        villagerAddress: String,
        villagerLatitude: Double? = null,
        villagerLongitude: Double? = null,
        medicineName: String,
        quantity: Int,
        shopId: String?,
        shopName: String? = null,
        prescriptionDocumentUri: String? = null,
    ) {
        val userId = auth.currentUser?.uid ?: return
        val profile = villagerProfile.value
        val cleanedAddress = villagerAddress.trim().ifBlank { profile?.address.orEmpty() }
        val request = hashMapOf(
            "villagerId" to userId,
            "villagerName" to villagerName.trim(),
            "villagerPhone" to villagerPhone.trim(),
            "villagerAddress" to cleanedAddress,
            "villagerLatitude" to (villagerLatitude ?: profile?.latitude ?: 0.0),
            "villagerLongitude" to (villagerLongitude ?: profile?.longitude ?: 0.0),
            "villagerRatingSum" to (profile?.ratingSum ?: 0.0),
            "villagerRatingCount" to (profile?.ratingCount ?: 0),
            "medicineName" to medicineName.trim(),
            "quantity" to quantity.coerceAtLeast(1),
            "shopId" to shopId,
            "shopName" to shopName.orEmpty(),
            "status" to "pending",
            "hasPrescription" to (prescriptionDocumentUri != null),
            "prescriptionDocumentUri" to prescriptionDocumentUri,
            "prescriptionUrl" to null,
            "villagerRated" to false,
            "pharmacistRated" to false,
            "issueReported" to false,
            "visibleToVillager" to true,
            "visibleToPharmacist" to true,
            "pharmacistMessage" to "",
            "createdAt" to com.google.firebase.Timestamp.now(),
        )
        firestore.collection(REQUESTS_COLLECTION).add(request)
    }

    fun ratePharmacist(request: ArrangementRequest, rating: Int) {
        if (request.pharmacistRated) return
        val shopId = request.shopId ?: return
        val clampedRating = rating.coerceIn(1, 5).toDouble()
        val batch = firestore.batch()
        batch.set(
            firestore.collection(USERS_COLLECTION).document(shopId),
            mapOf(
                "ratingSum" to FieldValue.increment(clampedRating),
                "ratingCount" to FieldValue.increment(1),
            ),
            SetOptions.merge(),
        )
        batch.set(
            firestore.collection(REQUESTS_COLLECTION).document(request.requestId),
            mapOf("pharmacistRated" to true),
            SetOptions.merge(),
        )
        batch.commit().addOnFailureListener { error ->
            Log.e(TAG, "Failed to rate pharmacist for requestId=${request.requestId}", error)
        }
    }

    fun reportIssue(requestId: String) {
        firestore.collection(REQUESTS_COLLECTION)
            .document(requestId)
            .set(mapOf("issueReported" to true), SetOptions.merge())
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to report issue for requestId=$requestId", error)
            }
    }

    fun clearRequestLog(requestId: String) {
        firestore.collection(REQUESTS_COLLECTION)
            .document(requestId)
            .set(mapOf("visibleToVillager" to false), SetOptions.merge())
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to hide request log for requestId=$requestId", error)
            }
    }

    fun clearRequestLogs(requestIds: Collection<String>) {
        if (requestIds.isEmpty()) return
        val batch = firestore.batch()
        requestIds.forEach { requestId ->
            batch.set(
                firestore.collection(REQUESTS_COLLECTION).document(requestId),
                mapOf("visibleToVillager" to false),
                SetOptions.merge(),
            )
        }
        batch.commit().addOnFailureListener { error ->
            Log.e(TAG, "Failed to hide selected villager request logs", error)
        }
    }

    fun logout() {
        auth.signOut()
    }

    override fun onCleared() {
        medicinesRegistration?.remove()
        medicinesRegistration = null
        super.onCleared()
    }
}

private fun MedicineItem.distanceFrom(location: VillagerLocationUiState): Double? {
    val villagerLatitude = location.latitude ?: return null
    val villagerLongitude = location.longitude ?: return null
    if (latitude == 0.0 && longitude == 0.0) return null

    return haversineDistanceKm(
        startLatitude = villagerLatitude,
        startLongitude = villagerLongitude,
        endLatitude = latitude,
        endLongitude = longitude,
    )
}

private fun MedicineItem.withPharmacyLocationFallback(
    pharmacy: PharmacyLocationSnapshot?,
): MedicineItem {
    if (pharmacy == null) return this
    val syncedLatitude = latitude.takeUnless { it == 0.0 } ?: pharmacy.latitude ?: 0.0
    val syncedLongitude = longitude.takeUnless { it == 0.0 } ?: pharmacy.longitude ?: 0.0
    return copy(
        shopName = shopName.ifBlank { pharmacy.shopName },
        shopPhone = shopPhone.ifBlank { pharmacy.phone },
        shopAddress = shopAddress.ifBlank { pharmacy.address },
        fullAddress = fullAddress.ifBlank { pharmacy.address },
        latitude = syncedLatitude,
        longitude = syncedLongitude,
    )
}

private fun haversineDistanceKm(
    startLatitude: Double,
    startLongitude: Double,
    endLatitude: Double,
    endLongitude: Double,
): Double {
    val earthRadiusKm = 6371.0
    val latitudeDelta = Math.toRadians(endLatitude - startLatitude)
    val longitudeDelta = Math.toRadians(endLongitude - startLongitude)
    val startLatitudeRadians = Math.toRadians(startLatitude)
    val endLatitudeRadians = Math.toRadians(endLatitude)

    val a = kotlin.math.sin(latitudeDelta / 2) * kotlin.math.sin(latitudeDelta / 2) +
        kotlin.math.cos(startLatitudeRadians) *
        kotlin.math.cos(endLatitudeRadians) *
        kotlin.math.sin(longitudeDelta / 2) *
        kotlin.math.sin(longitudeDelta / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return earthRadiusKm * c
}

private fun DocumentSnapshot.toUserProfile(): UserProfile = UserProfile(
    uid = getString("uid") ?: id,
    fullName = getString("fullName") ?: getString("ownerName").orEmpty(),
    email = getString("email").orEmpty(),
    role = UserRole.fromWireName(getString("role")) ?: UserRole.Villager,
    shopName = getString("shopName").orEmpty(),
    ownerName = getString("ownerName").orEmpty(),
    phone = getString("phone") ?: getString("contactNo").orEmpty(),
    address = getString("address").orEmpty(),
    latitude = numberAsDouble("latitude"),
    longitude = numberAsDouble("longitude"),
    age = (get("age") as? Number)?.toInt() ?: 0,
    ratingSum = numberAsDouble("ratingSum") ?: 0.0,
    ratingCount = (get("ratingCount") as? Number)?.toInt() ?: 0,
    isApproved = getBoolean("isApproved") ?: false,
)

private fun DocumentSnapshot.toArrangementRequest(): ArrangementRequest = ArrangementRequest(
    requestId = id,
    villagerId = getString("villagerId"),
    villagerName = getString("villagerName").orEmpty(),
    villagerPhone = getString("villagerPhone").orEmpty(),
    villagerAddress = getString("villagerAddress").orEmpty(),
    villagerLatitude = numberAsDouble("villagerLatitude"),
    villagerLongitude = numberAsDouble("villagerLongitude"),
    villagerRatingSum = numberAsDouble("villagerRatingSum") ?: 0.0,
    villagerRatingCount = (get("villagerRatingCount") as? Number)?.toInt() ?: 0,
    medicineName = getString("medicineName").orEmpty(),
    quantity = getLong("quantity")?.toInt() ?: 1,
    hasPrescription = getBoolean("hasPrescription") ?: false,
    status = getString("status").toArrangementStatus(),
    shopId = getString("shopId"),
    shopName = getString("shopName").orEmpty(),
    prescriptionDocumentUri = getString("prescriptionDocumentUri"),
    prescriptionUrl = getString("prescriptionUrl"),
    villagerRated = getBoolean("villagerRated") ?: false,
    pharmacistRated = getBoolean("pharmacistRated") ?: false,
    issueReported = getBoolean("issueReported") ?: false,
    visibleToVillager = getBoolean("visibleToVillager") ?: true,
    visibleToPharmacist = getBoolean("visibleToPharmacist") ?: true,
    pharmacistMessage = getString("pharmacistMessage").orEmpty(),
    createdAtMillis = getTimestamp("createdAt")?.toDate()?.time ?: 0L,
)

private fun String?.toArrangementStatus(): ArrangementStatus = when (this?.lowercase()) {
    "accepted" -> ArrangementStatus.Accepted
    "completed" -> ArrangementStatus.Completed
    "rejected", "declined" -> ArrangementStatus.Rejected
    else -> ArrangementStatus.Pending
}

private fun com.google.firebase.firestore.DocumentSnapshot.numberAsDouble(field: String): Double? =
    (get(field) as? Number)?.toDouble()

private fun DocumentSnapshot.expiryTimestamp(): Timestamp? {
    getTimestamp("expiryDate")?.let { return it }
    val expiryDateText = getString("expiryDate") ?: return null
    return runCatching {
        val instant = LocalDate.parse(expiryDateText)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
        Timestamp(Date.from(instant))
    }.getOrNull()
}
