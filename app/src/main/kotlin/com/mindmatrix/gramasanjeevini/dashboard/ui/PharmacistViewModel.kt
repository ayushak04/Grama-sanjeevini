package com.mindmatrix.gramasanjeevini.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mindmatrix.gramasanjeevini.data.ArrangementRequest
import com.mindmatrix.gramasanjeevini.data.ArrangementStatus
import com.mindmatrix.gramasanjeevini.data.MedicineItem
import com.mindmatrix.gramasanjeevini.data.PharmacistProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

data class ShopLocationUiState(
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    val hasCoordinates: Boolean
        get() = latitude != null && longitude != null
}

@HiltViewModel
class PharmacistViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ViewModel() {
    private companion object {
        const val TAG = "PharmacistViewModel"
        const val MEDICINES_COLLECTION = "medicines"
        const val USERS_COLLECTION = "users"
        const val REQUESTS_COLLECTION = "requests"
    }

    val shopLocation: StateFlow<ShopLocationUiState> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(ShopLocationUiState())
            awaitClose { }
            return@callbackFlow
        }

        val registration = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe pharmacist shop location", error)
                    trySend(ShopLocationUiState())
                    return@addSnapshotListener
                }

                trySend(
                    ShopLocationUiState(
                        address = snapshot?.getString("shopAddress")
                            ?: snapshot?.getString("fullAddress")
                            ?: snapshot?.getString("address")
                            ?: "",
                        latitude = snapshot?.numberAsDouble("latitude"),
                        longitude = snapshot?.numberAsDouble("longitude"),
                    ),
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShopLocationUiState())

    val pharmacistProfile: StateFlow<PharmacistProfile?> = callbackFlow {
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
                    Log.e(TAG, "Failed to observe pharmacist profile", error)
                    trySend(null)
                    return@addSnapshotListener
                }

                trySend(snapshot?.toPharmacistProfile())
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private data class RatingSnapshot(
        val ratingSum: Double = 0.0,
        val ratingCount: Int = 0,
    )

    private val rawIncomingRequests: StateFlow<List<ArrangementRequest>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val registration = firestore.collection(REQUESTS_COLLECTION)
            .whereEqualTo("visibleToPharmacist", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty()
                        .filter { document ->
                            val shopId = document.getString("shopId")
                            shopId == null || shopId == userId
                        }
                        .filter { document ->
                            document.getString("status").toArrangementStatus() in
                                listOf(ArrangementStatus.Pending, ArrangementStatus.Accepted)
                        }
                        .map { document ->
                            ArrangementRequest(
                                requestId = document.id,
                                villagerId = document.getString("villagerId"),
                                villagerName = document.getString("villagerName").orEmpty(),
                                villagerPhone = document.getString("villagerPhone").orEmpty(),
                                villagerAddress = document.getString("villagerAddress").orEmpty(),
                                villagerLatitude = document.numberAsDouble("villagerLatitude"),
                                villagerLongitude = document.numberAsDouble("villagerLongitude"),
                                villagerRatingSum = document.numberAsDouble("villagerRatingSum") ?: 0.0,
                                villagerRatingCount = (document.get("villagerRatingCount") as? Number)?.toInt() ?: 0,
                                medicineName = document.getString("medicineName").orEmpty(),
                                quantity = document.getLong("quantity")?.toInt() ?: 1,
                                hasPrescription = document.getBoolean("hasPrescription") ?: false,
                                status = document.getString("status").toArrangementStatus(),
                                shopId = document.getString("shopId"),
                                shopName = document.getString("shopName").orEmpty(),
                                prescriptionDocumentUri = document.getString("prescriptionDocumentUri"),
                                prescriptionUrl = document.getString("prescriptionUrl"),
                                villagerRated = document.getBoolean("villagerRated") ?: false,
                                pharmacistRated = document.getBoolean("pharmacistRated") ?: false,
                                issueReported = document.getBoolean("issueReported") ?: false,
                                visibleToVillager = document.getBoolean("visibleToVillager") ?: true,
                                visibleToPharmacist = document.getBoolean("visibleToPharmacist") ?: true,
                                pharmacistMessage = document.getString("pharmacistMessage").orEmpty(),
                                createdAtMillis = document.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                            )
                        }.sortedByDescending { request -> request.createdAtMillis },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val villagerRatings: StateFlow<Map<String, RatingSnapshot>> = callbackFlow {
        val registration = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("role", "villager")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe villager ratings", error)
                    trySend(emptyMap())
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty().associate { document ->
                        val userId = document.getString("uid") ?: document.id
                        userId to RatingSnapshot(
                            ratingSum = document.numberAsDouble("ratingSum") ?: 0.0,
                            ratingCount = (document.get("ratingCount") as? Number)?.toInt() ?: 0,
                        )
                    },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val incomingRequests: StateFlow<List<ArrangementRequest>> = combine(
        rawIncomingRequests,
        villagerRatings,
    ) { requests, ratings ->
        requests.map { request ->
            val rating = request.villagerId?.let(ratings::get)
            if (rating == null) {
                request
            } else {
                request.copy(
                    villagerRatingSum = rating.ratingSum,
                    villagerRatingCount = rating.ratingCount,
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val inventory: StateFlow<List<MedicineItem>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val registration = firestore.collection(MEDICINES_COLLECTION)
            .whereEqualTo("shopId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty()
                        .filter { document ->
                            document.getBoolean("isRemovedByAdmin") != true &&
                                document.getString("status") != "removed" &&
                                document.getBoolean("isDeletedByPharmacist") != true
                        }
                        .map { document ->
                            document.toMedicineItem()
                        },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateRequestStatus(requestId: String, status: ArrangementStatus, pharmacistMessage: String = "") {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection(REQUESTS_COLLECTION)
            .document(requestId)
            .set(
                mapOf(
                    "status" to status.name.lowercase(),
                    "shopId" to userId,
                    "pharmacistMessage" to pharmacistMessage.trim().take(60),
                ),
                SetOptions.merge(),
            )
    }

    fun completeRequestWithVillagerRating(request: ArrangementRequest, rating: Int) {
        val userId = auth.currentUser?.uid ?: return
        val clampedRating = rating.coerceIn(1, 5).toDouble()
        val batch = firestore.batch()
        val requestRef = firestore.collection(REQUESTS_COLLECTION).document(request.requestId)
        batch.set(
            requestRef,
            mapOf(
                "status" to ArrangementStatus.Completed.name.lowercase(),
                "shopId" to userId,
                "villagerRated" to true,
            ),
            SetOptions.merge(),
        )

        val villagerId = request.villagerId
        if (!villagerId.isNullOrBlank() && !request.villagerRated) {
            batch.set(
                firestore.collection(USERS_COLLECTION).document(villagerId),
                mapOf(
                    "ratingSum" to FieldValue.increment(clampedRating),
                    "ratingCount" to FieldValue.increment(1),
                ),
                SetOptions.merge(),
            )
        }

        batch.commit().addOnFailureListener { error ->
            Log.e(TAG, "Failed to complete requestId=${request.requestId}", error)
        }
    }

    fun deleteIncomingRequest(requestId: String) {
        auth.currentUser?.uid ?: return
        firestore.collection(REQUESTS_COLLECTION)
            .document(requestId)
            .set(mapOf("visibleToPharmacist" to false), SetOptions.merge())
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to hide incoming requestId=$requestId", error)
            }
    }

    fun clearIncomingRequests(requestIds: Collection<String>) {
        auth.currentUser?.uid ?: return
        if (requestIds.isEmpty()) return
        val batch = firestore.batch()
        requestIds.forEach { requestId ->
            batch.set(
                firestore.collection(REQUESTS_COLLECTION).document(requestId),
                mapOf("visibleToPharmacist" to false),
                SetOptions.merge(),
            )
        }
        batch.commit().addOnFailureListener { error ->
            Log.e(TAG, "Failed to hide selected incoming requests", error)
        }
    }

    fun updateMedicineStockStatus(medicineId: String, stockStatus: String) {
        auth.currentUser?.uid ?: return
        val document = firestore.collection(MEDICINES_COLLECTION).document(medicineId)
        document.get().addOnSuccessListener { snapshot ->
            val updatedMedicine = snapshot.toMedicineItem().copy(stockStatus = stockStatus)
            document.set(updatedMedicine)
                .addOnFailureListener { error ->
                    Log.e(TAG, "Failed to update stock status for medicineId=$medicineId", error)
                }
        }.addOnFailureListener { error ->
            Log.e(TAG, "Failed to load medicine before stock update: medicineId=$medicineId", error)
        }
    }

    fun deleteMedicine(medicineId: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection(MEDICINES_COLLECTION)
            .document(medicineId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.getString("shopId") == userId) {
                    snapshot.reference.set(
                        mapOf(
                            "isDeletedByPharmacist" to true,
                            "visibleToVillager" to false,
                        ),
                        SetOptions.merge(),
                    )
                }
            }
    }

    fun clearInventory(medicineIds: Collection<String>) {
        auth.currentUser?.uid ?: return
        if (medicineIds.isEmpty()) return
        val batch = firestore.batch()
        medicineIds.forEach { medicineId ->
            batch.set(
                firestore.collection(MEDICINES_COLLECTION).document(medicineId),
                mapOf(
                    "isDeletedByPharmacist" to true,
                    "visibleToVillager" to false,
                ),
                SetOptions.merge(),
            )
        }
        batch.commit()
    }

    fun updateShopLocation(
        address: String,
        latitude: Double?,
        longitude: Double?,
    ) {
        val userId = auth.currentUser?.uid ?: return
        val cleanedAddress = address.trim()
        val locationFields = mutableMapOf<String, Any>(
            "address" to cleanedAddress,
            "shopAddress" to cleanedAddress,
            "fullAddress" to cleanedAddress,
        )

        if (latitude != null && longitude != null) {
            locationFields["latitude"] = latitude
            locationFields["longitude"] = longitude
        } else {
            locationFields["latitude"] = 0.0
            locationFields["longitude"] = 0.0
        }

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(locationFields, SetOptions.merge())
            .addOnSuccessListener {
                propagateShopLocationToMedicines(userId, cleanedAddress, latitude, longitude)
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to update shop location for userId=$userId", error)
            }
    }

    fun updatePharmacistProfile(
        shopName: String,
        ownerName: String,
        phone: String,
        address: String,
        latitude: Double?,
        longitude: Double?,
        drugLicenseNumber: String,
        pharmacistRegNumber: String,
        gstin: String,
        shopTimings: String,
        hasFssai: Boolean,
        fssaiNumber: String,
    ) {
        val userId = auth.currentUser?.uid ?: return
        val cleanedShopName = shopName.trim()
        val cleanedOwnerName = ownerName.trim()
        val cleanedPhone = phone.trim()
        val cleanedAddress = address.trim()
        val cleanedDrugLicenseNumber = drugLicenseNumber.trim()
        val cleanedPharmacistRegNumber = pharmacistRegNumber.trim()
        val cleanedGstin = gstin.trim()
        val cleanedShopTimings = shopTimings.trim()
        val cleanedFssaiNumber = if (hasFssai) fssaiNumber.trim() else ""
        val fields = mapOf(
            "shopName" to cleanedShopName,
            "ownerName" to cleanedOwnerName,
            "fullName" to cleanedOwnerName,
            "phone" to cleanedPhone,
            "shopPhone" to cleanedPhone,
            "drugLicenseNumber" to cleanedDrugLicenseNumber,
            "pharmacistRegNumber" to cleanedPharmacistRegNumber,
            "gstin" to cleanedGstin,
            "shopTimings" to cleanedShopTimings,
            "hasFssai" to hasFssai,
            "fssaiNumber" to cleanedFssaiNumber,
            "address" to cleanedAddress,
            "shopAddress" to cleanedAddress,
            "fullAddress" to cleanedAddress,
            "latitude" to (latitude ?: 0.0),
            "longitude" to (longitude ?: 0.0),
        )

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(fields, SetOptions.merge())
            .addOnSuccessListener {
                propagatePharmacistProfileToMedicines(
                    userId = userId,
                    shopName = cleanedShopName,
                    phone = cleanedPhone,
                    address = cleanedAddress,
                    latitude = latitude,
                    longitude = longitude,
                )
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to update pharmacist profile for userId=$userId", error)
            }
    }

    fun addMedicine(
        name: String,
        quantity: Int,
        stockStatus: String,
        requiresPrescription: Boolean,
        isLifeSaving: Boolean,
        expiryDateMillis: Long?,
    ) {
        val userId = auth.currentUser?.uid ?: return
        val medicineId = firestore.collection(MEDICINES_COLLECTION).document().id
        firestore.collection(USERS_COLLECTION).document(userId).get()
            .addOnSuccessListener { profile ->
                val medicineItem = MedicineItem(
                    id = medicineId,
                    medicineName = name.trim(),
                    isLifeSaving = isLifeSaving,
                    shopId = userId,
                    shopName = profile.getString("shopName").orEmpty(),
                    shopArea = profile.getString("shopArea")
                        ?: profile.getString("area")
                        ?: profile.getString("village")
                        ?: "",
                    shopAddress = profile.getString("shopAddress")
                        ?: profile.getString("fullAddress")
                        ?: profile.getString("address")
                        ?: "",
                    fullAddress = profile.getString("fullAddress")
                        ?: profile.getString("address")
                        ?: "",
                    distanceKm = null,
                    stockStatus = stockStatus,
                    requiresPrescription = requiresPrescription,
                    expiryDate = expiryDateMillis?.let { Timestamp(Date(it)) },
                    quantity = quantity.coerceAtLeast(0),
                    shopPhone = profile.getString("shopPhone")
                        ?: profile.getString("phone")
                        ?: "",
                    latitude = profile.numberAsDouble("latitude") ?: 0.0,
                    longitude = profile.numberAsDouble("longitude") ?: 0.0,
                )
                firestore.collection(MEDICINES_COLLECTION)
                    .document(medicineId)
                    .set(medicineItem)
                    .addOnFailureListener { error ->
                        Log.e(TAG, "Failed to add medicine: medicineId=$medicineId", error)
                    }
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to load pharmacist profile before adding medicine", error)
            }
    }

    fun logout() {
        viewModelScope.coroutineContext.cancelChildren()
        auth.signOut()
    }

    private fun propagatePharmacistProfileToMedicines(
        userId: String,
        shopName: String,
        phone: String,
        address: String,
        latitude: Double?,
        longitude: Double?,
    ) {
        firestore.collection(MEDICINES_COLLECTION)
            .whereEqualTo("shopId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()
                val fields = mapOf(
                    "shopName" to shopName,
                    "shopPhone" to phone,
                    "shopAddress" to address,
                    "fullAddress" to address,
                    "latitude" to (latitude ?: 0.0),
                    "longitude" to (longitude ?: 0.0),
                )

                snapshot.documents.forEach { document ->
                    batch.set(document.reference, fields, SetOptions.merge())
                }
                batch.commit().addOnFailureListener { error ->
                    Log.e(TAG, "Failed to propagate pharmacist profile to medicines for userId=$userId", error)
                }
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to load medicines before profile propagation for userId=$userId", error)
            }
    }

    private fun propagateShopLocationToMedicines(
        userId: String,
        address: String,
        latitude: Double?,
        longitude: Double?,
    ) {
        firestore.collection(MEDICINES_COLLECTION)
            .whereEqualTo("shopId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = firestore.batch()
                val fields = mapOf(
                    "shopAddress" to address,
                    "fullAddress" to address,
                    "latitude" to (latitude ?: 0.0),
                    "longitude" to (longitude ?: 0.0),
                )

                snapshot.documents.forEach { document ->
                    batch.set(document.reference, fields, SetOptions.merge())
                }
                batch.commit()
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to propagate shop location to medicines for userId=$userId", error)
            }
    }
}

private fun String?.toArrangementStatus(): ArrangementStatus = when (this?.lowercase()) {
    "accepted" -> ArrangementStatus.Accepted
    "completed" -> ArrangementStatus.Completed
    "rejected", "declined" -> ArrangementStatus.Rejected
    else -> ArrangementStatus.Pending
}

private fun DocumentSnapshot.toMedicineItem(): MedicineItem {
    return MedicineItem(
        id = getString("id") ?: getString("medicineId") ?: id,
        medicineName = getString("medicineName") ?: getString("name").orEmpty(),
        isLifeSaving = getBoolean("isLifeSaving") ?: false,
        shopId = getString("shopId") ?: getString("pharmacistId").orEmpty(),
        shopName = getString("shopName").orEmpty(),
        shopArea = getString("shopArea") ?: getString("area") ?: getString("village") ?: "",
        shopAddress = getString("shopAddress") ?: getString("fullAddress") ?: getString("address") ?: "",
        fullAddress = getString("fullAddress") ?: getString("address") ?: "",
        distanceKm = null,
        stockStatus = getString("stockStatus")
            ?: if (getBoolean("inStock") == true) "Available" else "Out of Stock",
        requiresPrescription = getBoolean("requiresPrescription") ?: false,
        expiryDate = expiryTimestamp(),
        quantity = getLong("quantity")?.toInt() ?: 0,
        shopPhone = getString("shopPhone") ?: getString("phone") ?: "",
        latitude = numberAsDouble("latitude") ?: 0.0,
        longitude = numberAsDouble("longitude") ?: 0.0,
    )
}

private fun DocumentSnapshot.toPharmacistProfile(): PharmacistProfile =
    PharmacistProfile(
        uid = getString("uid") ?: id,
        email = getString("email").orEmpty(),
        shopName = getString("shopName").orEmpty(),
        ownerName = getString("ownerName").orEmpty(),
        drugLicenseNumber = getString("drugLicenseNumber").orEmpty(),
        pharmacistRegNumber = getString("pharmacistRegNumber").orEmpty(),
        gstin = getString("gstin").orEmpty(),
        shopTimings = getString("shopTimings").orEmpty(),
        hasFssai = getBoolean("hasFssai") ?: false,
        fssaiNumber = getString("fssaiNumber").orEmpty(),
        phone = getString("phone").orEmpty(),
        address = getString("shopAddress") ?: getString("fullAddress") ?: getString("address").orEmpty(),
        latitude = numberAsDouble("latitude"),
        longitude = numberAsDouble("longitude"),
        isApproved = getBoolean("isApproved") ?: false,
        role = getString("role") ?: "pharmacist",
        ratingSum = numberAsDouble("ratingSum") ?: 0.0,
        ratingCount = (get("ratingCount") as? Number)?.toInt() ?: 0,
    )

private fun DocumentSnapshot.numberAsDouble(field: String): Double? =
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
