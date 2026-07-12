package com.mindmatrix.gramasanjeevini.auth.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mindmatrix.gramasanjeevini.auth.data.AuthRepository
import com.mindmatrix.gramasanjeevini.data.ArrangementRequest
import com.mindmatrix.gramasanjeevini.data.ArrangementStatus
import com.mindmatrix.gramasanjeevini.data.PharmacistProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore,
) : ViewModel() {
    private companion object {
        const val TAG = "AdminViewModel"
        const val REQUESTS_COLLECTION = "requests"
        const val USERS_COLLECTION = "users"
        const val MEDICINES_COLLECTION = "medicines"
    }

    private val _requestClearState = MutableStateFlow(AdminRequestClearUiState())
    val requestClearState: StateFlow<AdminRequestClearUiState> = _requestClearState.asStateFlow()

    private var logoutStarted = false

    val pendingPharmacies: StateFlow<List<PharmacistProfile>> = callbackFlow {
        val registration = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("role", "pharmacist")
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe pending pharmacies", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty()
                        .filter { document ->
                            document.getBoolean("isBanned") != true &&
                                document.getBoolean("isRemovedByAdmin") != true &&
                                document.getString("status") != "removed"
                        }
                        .map { document -> document.toPharmacistProfile() },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val activePharmacies: StateFlow<List<PharmacistProfile>> = callbackFlow {
        val registration = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("role", "pharmacist")
            .whereEqualTo("isApproved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe active pharmacies", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty()
                        .filter { document ->
                            document.getBoolean("isRemovedByAdmin") != true &&
                                document.getString("status") != "removed"
                        }
                        .map { document -> document.toPharmacistProfile() },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val activePharmacyCount: StateFlow<Int> = callbackFlow {
        val registration = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("role", "pharmacist")
            .whereEqualTo("isApproved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe active pharmacy count", error)
                    trySend(0)
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty().count { document ->
                        document.getBoolean("isRemovedByAdmin") != true &&
                            document.getString("status") != "removed"
                    },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val villagerCount: StateFlow<Int> = callbackFlow {
        val registration = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("role", "villager")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe villager count", error)
                    trySend(0)
                    return@addSnapshotListener
                }

                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val requestCount: StateFlow<Int> = callbackFlow {
        val registration = firestore.collection(REQUESTS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe request count", error)
                    trySend(0)
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty().count { document ->
                        document.getBoolean("isDeletedByAdmin") != true &&
                            document.getBoolean("visibleToAdmin") != false
                    },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val allRequests: StateFlow<List<ArrangementRequest>> = callbackFlow {
        val registration = firestore.collection(REQUESTS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to observe request logs", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                trySend(
                    snapshot?.documents.orEmpty()
                        .filter { document ->
                            document.getBoolean("isDeletedByAdmin") != true &&
                                document.getBoolean("visibleToAdmin") != false
                        }
                        .map { document ->
                            ArrangementRequest(
                                requestId = document.id,
                                villagerId = document.getString("villagerId"),
                                villagerName = document.getString("villagerName").orEmpty(),
                                villagerPhone = document.getString("villagerPhone").orEmpty(),
                                villagerAddress = document.getString("villagerAddress").orEmpty(),
                                villagerLatitude = (document.get("villagerLatitude") as? Number)?.toDouble(),
                                villagerLongitude = (document.get("villagerLongitude") as? Number)?.toDouble(),
                                villagerRatingSum = (document.get("villagerRatingSum") as? Number)?.toDouble() ?: 0.0,
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
                        }.sortedByDescending { it.createdAtMillis },
                )
            }
        awaitClose { registration.remove() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val overview: StateFlow<AdminOverviewUiState> = combine(
        activePharmacyCount,
        villagerCount,
        requestCount,
    ) { activePharmacies, villagers, requests ->
        AdminOverviewUiState(
            activePharmacies = activePharmacies,
            villagersRegistered = villagers,
            totalRequests = requests,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AdminOverviewUiState())

    fun approvePharmacy(uid: String) {
        firestore.collection("users").document(uid).update(
            mapOf(
                "isApproved" to true,
                "isBanned" to false,
                "isRemovedByAdmin" to false,
                "status" to "active",
            ),
        )
    }

    fun removePharmacy(shopId: String) {
        removePharmacyAndHideMedicines(shopId)
    }

    fun rejectPharmacy(uid: String) {
        removePharmacyAndHideMedicines(uid)
    }

    fun deleteRequest(requestId: String) {
        deleteRequests(listOf(requestId))
    }

    fun deleteRequests(requestIds: Collection<String>) {
        val cleanedIds = requestIds.filter { it.isNotBlank() }.distinct()
        if (cleanedIds.isEmpty()) return

        viewModelScope.launch {
            hideRequestDocuments(
                requestIds = cleanedIds,
                successMessage = "Selected request logs cleared.",
            )
        }
    }

    fun clearAllRequests() {
        viewModelScope.launch {
            val visibleRequestIds = runCatching {
                firestore.collection(REQUESTS_COLLECTION)
                    .get()
                    .await()
                    .documents
                    .filter { document ->
                        document.getBoolean("isDeletedByAdmin") != true &&
                            document.getBoolean("visibleToAdmin") != false
                    }
                    .map { document -> document.id }
            }.getOrElse { error ->
                Log.e(TAG, "Failed to load request logs before clearing all", error)
                _requestClearState.value = AdminRequestClearUiState(
                    message = "Could not clear request logs. Please try again.",
                    isError = true,
                )
                return@launch
            }

            hideRequestDocuments(
                requestIds = visibleRequestIds,
                successMessage = "All request logs cleared.",
            )
        }
    }

    fun clearRequestClearMessage() {
        _requestClearState.value = _requestClearState.value.copy(message = null, isError = false)
    }

    private fun removePharmacyAndHideMedicines(shopId: String) {
        firestore.collection(MEDICINES_COLLECTION)
            .whereEqualTo("shopId", shopId)
            .get()
            .addOnSuccessListener { medicinesSnapshot ->
                val batch = firestore.batch()
                val pharmacistRef = firestore.collection(USERS_COLLECTION).document(shopId)

                batch.update(
                    pharmacistRef,
                    mapOf(
                        "isApproved" to false,
                        "isBanned" to true,
                        "isRemovedByAdmin" to true,
                        "status" to "removed",
                    ),
                )

                medicinesSnapshot.documents.forEach { medicineDocument ->
                    batch.set(
                        medicineDocument.reference,
                        mapOf(
                            "isRemovedByAdmin" to true,
                            "visibleToVillager" to false,
                            "status" to "removed",
                        ),
                        SetOptions.merge(),
                    )
                }

                batch.commit()
            }
    }

    fun logout() {
        if (logoutStarted) return
        logoutStarted = true
        viewModelScope.coroutineContext.cancelChildren()
        authRepository.logout()
    }

    private suspend fun hideRequestDocuments(
        requestIds: Collection<String>,
        successMessage: String,
    ) {
        if (_requestClearState.value.isClearing) return

        _requestClearState.value = AdminRequestClearUiState(isClearing = true)
        runCatching {
            val cleanedIds = requestIds.filter { it.isNotBlank() }.distinct()
            cleanedIds.chunked(450).forEach { chunk ->
                val batch = firestore.batch()
                chunk.forEach { requestId ->
                    batch.set(
                        firestore.collection(REQUESTS_COLLECTION).document(requestId),
                        mapOf(
                            "isDeletedByAdmin" to true,
                            "visibleToAdmin" to false,
                        ),
                        SetOptions.merge(),
                    )
                }
                batch.commit().await()
            }
            cleanedIds.size
        }.onSuccess { clearedCount ->
            _requestClearState.value = AdminRequestClearUiState(
                message = if (clearedCount == 0) "No request logs to clear." else successMessage,
                isError = false,
            )
        }.onFailure { error ->
            Log.e(TAG, "Failed to clear request logs", error)
            _requestClearState.value = AdminRequestClearUiState(
                message = "Could not clear request logs. Please try again.",
                isError = true,
            )
        }
    }
}

data class AdminRequestClearUiState(
    val isClearing: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
)

data class AdminOverviewUiState(
    val activePharmacies: Int = 0,
    val villagersRegistered: Int = 0,
    val totalRequests: Int = 0,
)

private fun com.google.firebase.firestore.DocumentSnapshot.toPharmacistProfile(): PharmacistProfile =
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
        address = getString("address").orEmpty(),
        latitude = (get("latitude") as? Number)?.toDouble(),
        longitude = (get("longitude") as? Number)?.toDouble(),
        isApproved = getBoolean("isApproved") ?: false,
        role = getString("role") ?: "pharmacist",
        ratingSum = (get("ratingSum") as? Number)?.toDouble() ?: 0.0,
        ratingCount = (get("ratingCount") as? Number)?.toInt() ?: 0,
    )

private fun String?.toArrangementStatus(): ArrangementStatus = when (this?.lowercase()) {
    "accepted" -> ArrangementStatus.Accepted
    "completed" -> ArrangementStatus.Completed
    "rejected", "declined" -> ArrangementStatus.Rejected
    else -> ArrangementStatus.Pending
}
