package com.mindmatrix.gramasanjeevini.dashboard.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mindmatrix.gramasanjeevini.auth.domain.UserProfile
import coil.compose.AsyncImage
import com.mindmatrix.gramasanjeevini.auth.ui.AdminRequestClearUiState
import com.mindmatrix.gramasanjeevini.auth.ui.AdminViewModel
import com.mindmatrix.gramasanjeevini.data.ArrangementRequest
import com.mindmatrix.gramasanjeevini.data.ArrangementStatus
import com.mindmatrix.gramasanjeevini.data.MedicineItem
import com.mindmatrix.gramasanjeevini.data.MedicineSearchResult
import com.mindmatrix.gramasanjeevini.data.PharmacistProfile
import com.mindmatrix.gramasanjeevini.data.ShopSearchResult
import com.mindmatrix.gramasanjeevini.data.ShopResult
import com.mindmatrix.gramasanjeevini.data.VillageLocation
import com.mindmatrix.gramasanjeevini.core.NativeLocationPermissions
import com.mindmatrix.gramasanjeevini.core.fetchNativeLiveLocation
import com.mindmatrix.gramasanjeevini.core.hasNativeLocationPermission
import com.mindmatrix.gramasanjeevini.core.isApproximateAppLocation
import com.mindmatrix.gramasanjeevini.ui.AvailabilityGreen
import com.mindmatrix.gramasanjeevini.ui.EmergencyRed
import com.mindmatrix.gramasanjeevini.data.ratingDisplayText
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val DashboardRadius = 12.dp

@Composable
fun VillagerDashboardScreen(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onBackToRoles: () -> Unit,
    forceGuestMode: Boolean = false,
    viewModel: VillagerViewModel = hiltViewModel(),
) {
    VillagerDashboard(
        darkTheme = darkTheme,
        onDarkThemeChanged = onDarkThemeChanged,
        viewModel = viewModel,
        onBackToRoles = onBackToRoles,
        forceGuestMode = forceGuestMode,
    )
}

@Composable
fun PharmacistDashboardScreen(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onLogoutComplete: () -> Unit,
    viewModel: PharmacistViewModel = hiltViewModel(),
) {
    PharmacistDashboard(
        darkTheme = darkTheme,
        onDarkThemeChanged = onDarkThemeChanged,
        viewModel = viewModel,
        onLogout = {
            viewModel.logout()
            onLogoutComplete()
        },
    )
}

@Composable
fun AdminDashboardScreen(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onLogoutComplete: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel(),
) {
    AdminDashboard(
        darkTheme = darkTheme,
        onDarkThemeChanged = onDarkThemeChanged,
        viewModel = viewModel,
        onLogout = {
            viewModel.logout()
            onLogoutComplete()
        },
    )
}

private enum class PharmacistDashboardTab {
    Profile,
    Requests,
    Inventory,
    Alerts,
}

private enum class AdminDashboardTab {
    Overview,
    VerifyPharmacies,
    ManageShops,
    RequestLogs,
}

@Composable
private fun ConfirmationDialog(
    title: String,
    text: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = if (isDestructive) EmergencyRed else MaterialTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun VillagerDashboard(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    viewModel: VillagerViewModel,
    onBackToRoles: () -> Unit,
    forceGuestMode: Boolean = false,
) {
    var selectedSection by remember { mutableStateOf(DashboardSection.Home) }
    var selectedSearchTab by remember { mutableStateOf(SearchRequestTab.FindMedicines) }
    var selectedShop by remember { mutableStateOf<ShopResult?>(null) }
    var showGuestAuthDialog by remember { mutableStateOf(false) }
    var ratingRequest by remember { mutableStateOf<ArrangementRequest?>(null) }
    val medicineState by viewModel.medicineState.collectAsState()
    val villagerLocation by viewModel.villagerLocation.collectAsState()
    val shops by viewModel.approvedShops.collectAsState()
    val profile by viewModel.villagerProfile.collectAsState()
    val requestLogs by viewModel.requestLogs.collectAsState()
    val authGuest by viewModel.isGuest.collectAsState()
    val isGuest = forceGuestMode || authGuest

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            DashboardTopBar(
                darkTheme = darkTheme,
                onDarkThemeChanged = onDarkThemeChanged,
                onLogout = {
                    viewModel.logout()
                    onBackToRoles()
                },
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = selectedSection == DashboardSection.Home,
                    onClick = { selectedSection = DashboardSection.Home },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                    label = { Text("Home") },
                    colors = dashboardNavigationItemColors(),
                )
                NavigationBarItem(
                    selected = selectedSection == DashboardSection.Search,
                    onClick = { selectedSection = DashboardSection.Search },
                    icon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    label = { Text("Search") },
                    colors = dashboardNavigationItemColors(),
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (selectedSection == DashboardSection.Search) {
                SearchRequestTabRow(
                    selectedTab = selectedSearchTab,
                    onSelectedTab = { tab ->
                        if (tab == SearchRequestTab.Profile) {
                            selectedSection = DashboardSection.Home
                        } else {
                            selectedSearchTab = tab
                        }
                    },
                )
            }
            when (selectedSection) {
                DashboardSection.Home -> VillagerHomeScreen(
                    profile = if (isGuest) null else profile,
                    isGuest = isGuest,
                    onUpdateDetails = { fullName, age, address, latitude, longitude ->
                        viewModel.updateVillagerProfile(fullName, age, address, latitude, longitude)
                    },
                )
                DashboardSection.Search -> when (selectedSearchTab) {
                    SearchRequestTab.Profile -> VillagerHomeScreen(
                        profile = if (isGuest) null else profile,
                        isGuest = isGuest,
                        onUpdateDetails = { fullName, age, address, latitude, longitude ->
                            viewModel.updateVillagerProfile(fullName, age, address, latitude, longitude)
                        },
                    )
                    SearchRequestTab.FindMedicines -> {
                        if (isGuest) {
                            GuestTemporaryLocationCard(
                                hasCoordinates = villagerLocation.hasCoordinates,
                                onLocationFetched = viewModel::updateVillagerLiveLocation,
                            )
                        }
                        RealtimeMedicinesStateContent(
                            state = medicineState,
                            title = "Find Medicines",
                            locationRequiredForRadius = villagerLocation.hasCoordinates,
                        )
                    }
                    SearchRequestTab.DirectShopRequest -> {
                        if (isGuest) {
                            GuestTemporaryLocationCard(
                                hasCoordinates = villagerLocation.hasCoordinates,
                                onLocationFetched = viewModel::updateVillagerLiveLocation,
                            )
                        }
                        RealtimeShopsList(
                            shops = shops,
                            villagerLocation = if (isGuest) {
                                villagerLocation
                            } else {
                                profile?.takeIf { it.latitude != null && it.longitude != null }?.let {
                                VillagerLocationUiState(
                                    address = it.address,
                                    latitude = it.latitude,
                                    longitude = it.longitude,
                                )
                                } ?: villagerLocation
                            },
                            title = "Direct Shop Request",
                            isGuest = isGuest,
                            onArrangementRequest = { shop ->
                                if (isGuest) {
                                    showGuestAuthDialog = true
                                } else {
                                    selectedShop = shop
                                }
                            },
                        )
                    }
                    SearchRequestTab.RequestLogs -> {
                        if (isGuest) {
                            GuestLockedFeatureMessage()
                        } else {
                            VillagerRequestLogs(
                                requests = requestLogs,
                                onRatePharmacist = { request -> ratingRequest = request },
                                onReportIssue = viewModel::reportIssue,
                                onClearSelected = viewModel::clearRequestLogs,
                            )
                        }
                    }
                }
                DashboardSection.Emergency -> RealtimeMedicinesStateContent(
                    state = medicineState,
                    title = "Emergency medicines",
                    filter = { it.requiresPrescription || it.isLifeSaving },
                    emptyText = "No emergency medicines are available yet.",
                    locationRequiredForRadius = villagerLocation.hasCoordinates,
                )
                DashboardSection.Stock -> Unit
            }
        }
    }

    selectedShop?.let { shop ->
        FirestoreArrangementBottomSheet(
            shop = shop,
            profile = profile,
            onDismiss = { selectedShop = null },
            onSubmit = { villagerName, villagerPhone, villagerAddress, villagerLatitude, villagerLongitude, medicineName, quantity, prescriptionUri ->
                viewModel.submitRequest(
                    villagerName = villagerName,
                    villagerPhone = villagerPhone,
                    villagerAddress = villagerAddress,
                    villagerLatitude = villagerLatitude,
                    villagerLongitude = villagerLongitude,
                    medicineName = medicineName,
                    quantity = quantity,
                    shopId = shop.uid,
                    shopName = shop.shopName,
                    prescriptionDocumentUri = prescriptionUri,
                )
                selectedShop = null
            },
        )
    }

    if (showGuestAuthDialog) {
        AlertDialog(
            onDismissRequest = { showGuestAuthDialog = false },
            title = { Text("Login required") },
            text = { Text("Please login or signup to use this feature.") },
            confirmButton = {
                TextButton(onClick = { showGuestAuthDialog = false }) {
                    Text("OK")
                }
            },
        )
    }

    ratingRequest?.let { request ->
        RatingDialog(
            title = "Rate Pharmacist",
            targetName = request.shopName.ifBlank { "Pharmacist" },
            onDismiss = { ratingRequest = null },
            onSubmit = { rating ->
                viewModel.ratePharmacist(request, rating)
                ratingRequest = null
            },
        )
    }
}

@Composable
private fun VillagerHomeScreen(
    profile: UserProfile?,
    isGuest: Boolean,
    onUpdateDetails: (String, Int, String, Double?, Double?) -> Unit,
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val displayName = profile?.fullName?.takeIf { it.isNotBlank() } ?: if (isGuest) "Guest Villager" else "Villager"

    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (isGuest) "Login to save details and track requests." else "Your profile",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp),
                )
            }

            ProfileInfoRow(label = "Mobile Number", value = profile?.phone.orEmpty().ifBlank { "Unavailable" })
            ProfileInfoRow(label = "Email", value = profile?.email.orEmpty().ifBlank { "Unavailable" })
            ProfileInfoRow(
                label = "Age",
                value = profile?.age?.takeIf { it > 0 }?.toString() ?: "Not added",
            )
            ProfileInfoRow(label = "Address", value = profile?.address.orEmpty().ifBlank { "Not added" })
            ProfileInfoRow(
                label = "Overall Rating",
                value = ratingDisplayText(profile?.ratingSum ?: 0.0, profile?.ratingCount ?: 0),
            )

            Button(
                onClick = { showEditDialog = true },
                enabled = !isGuest && profile != null,
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Edit your Details")
            }
        }
    }

    if (showEditDialog && profile != null) {
        EditVillagerDetailsDialog(
            profile = profile,
            onDismiss = { showEditDialog = false },
            onSave = { fullName, age, address, latitude, longitude ->
                onUpdateDetails(fullName, age, address, latitude, longitude)
                showEditDialog = false
            },
        )
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun EditVillagerDetailsDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, Int, String, Double?, Double?) -> Unit,
) {
    val context = LocalContext.current
    var fullName by remember(profile.uid) { mutableStateOf(profile.fullName) }
    var ageText by remember(profile.uid) { mutableStateOf(profile.age.takeIf { it > 0 }?.toString().orEmpty()) }
    var address by remember(profile.uid) { mutableStateOf(profile.address) }
    var latitude by remember(profile.uid) { mutableStateOf(profile.latitude) }
    var longitude by remember(profile.uid) { mutableStateOf(profile.longitude) }
    var statusMessage by remember(profile.uid) { mutableStateOf<String?>(null) }
    val age = ageText.toIntOrNull() ?: 0
    val canSave = fullName.trim().length >= 2 && age in 0..120 && address.trim().isNotBlank()
    fun fetchAndSaveLocation(permissionDenied: Boolean = false) {
        statusMessage = "Fetching your live location..."
        fetchNativeLiveLocation(
            context = context,
            fallbackAddress = address,
            onLocationFetched = { liveLocation ->
                latitude = liveLocation.latitude
                longitude = liveLocation.longitude
                statusMessage = locationSavedMessage(
                    liveLocation = liveLocation,
                    liveText = "Live location saved privately.",
                    approximateText = if (permissionDenied) {
                        "Permission denied. Approximate location saved privately."
                    } else {
                        "Approximate location saved privately."
                    },
                )
            },
            onLocationUnavailable = {
                statusMessage = "No live location was available. Try again near an open map signal."
            },
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            fetchAndSaveLocation()
        } else {
            fetchAndSaveLocation(permissionDenied = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit your Details") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it.take(60) },
                    label = { Text("Name") },
                    singleLine = true,
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it.filter(Char::isDigit).take(3) },
                    label = { Text("Age") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it.take(180) },
                    label = { Text("Full Address / Landmark") },
                    minLines = 2,
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(
                    onClick = {
                        if (hasNativeLocationPermission(context)) {
                            fetchAndSaveLocation()
                        } else {
                            locationPermissionLauncher.launch(NativeLocationPermissions)
                        }
                    },
                    shape = RoundedCornerShape(DashboardRadius),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Fetch Live Location")
                }
                statusMessage?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(fullName, age, address, latitude, longitude) },
                enabled = canSave,
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun VillagerRequestLogs(
    requests: List<ArrangementRequest>,
    onRatePharmacist: (ArrangementRequest) -> Unit,
    onReportIssue: (String) -> Unit,
    onClearSelected: (Collection<String>) -> Unit,
) {
    var selectedRequestIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var requestIdsPendingClear by remember { mutableStateOf<Set<String>?>(null) }
    Text(
        text = "Request Logs",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    if (requests.isEmpty()) {
        EmptyStateText("No medicine arrangement requests yet.")
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = {
                    requestIdsPendingClear = selectedRequestIds
                },
                enabled = selectedRequestIds.isNotEmpty(),
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.weight(1f),
            ) {
                Text("Clear selected")
            }
            FilledTonalButton(
                onClick = {
                    requestIdsPendingClear = requests.map { it.requestId }.toSet()
                },
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.weight(1f),
            ) {
                Text("Clear all")
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            requests.forEach { request ->
                VillagerRequestLogCard(
                    request = request,
                    selected = request.requestId in selectedRequestIds,
                    onSelectedChanged = { selected ->
                        selectedRequestIds = if (selected) {
                            selectedRequestIds + request.requestId
                        } else {
                            selectedRequestIds - request.requestId
                        }
                    },
                    onRatePharmacist = { onRatePharmacist(request) },
                    onReportIssue = { onReportIssue(request.requestId) },
                )
            }
        }
    }

    requestIdsPendingClear?.let { requestIds ->
        ConfirmationDialog(
            title = "Clear Logs",
            text = "Are you sure you want to remove these logs from your view?",
            confirmText = "Clear",
            onConfirm = {
                onClearSelected(requestIds)
                selectedRequestIds = emptySet()
                requestIdsPendingClear = null
            },
            onDismiss = { requestIdsPendingClear = null },
            isDestructive = true,
        )
    }
}

@Composable
private fun GuestLockedFeatureMessage() {
    Surface(
        shape = RoundedCornerShape(DashboardRadius),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Please login or signup to use this feature.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(18.dp),
        )
    }
}

@Composable
private fun GuestTemporaryLocationCard(
    hasCoordinates: Boolean,
    onLocationFetched: (Double, Double) -> Unit,
) {
    val context = LocalContext.current
    var statusMessage by remember(hasCoordinates) {
        mutableStateOf(
            if (hasCoordinates) {
                "Live location saved."
            } else {
                null
            },
        )
    }
    fun fetchAndSaveLocation(permissionDenied: Boolean = false) {
        statusMessage = "Fetching your live location..."
        fetchNativeLiveLocation(
            context = context,
            onLocationFetched = { liveLocation ->
                onLocationFetched(liveLocation.latitude, liveLocation.longitude)
                statusMessage = locationSavedMessage(
                    liveLocation = liveLocation,
                    liveText = "Live location saved.",
                    approximateText = if (permissionDenied) {
                        "Permission denied. Approximate location saved."
                    } else {
                        "Approximate location saved."
                    },
                )
            },
            onLocationUnavailable = {
                statusMessage = "No live location was available. Try again near an open signal."
            },
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            fetchAndSaveLocation()
        } else {
            fetchAndSaveLocation(permissionDenied = true)
        }
    }

    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = {
                    if (hasNativeLocationPermission(context)) {
                        fetchAndSaveLocation()
                    } else {
                        locationPermissionLauncher.launch(NativeLocationPermissions)
                    }
                },
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Rounded.LocationOn, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Fetch Live Location")
            }
            statusMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (hasCoordinates) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun VillagerRequestLogCard(
    request: ArrangementRequest,
    selected: Boolean,
    onSelectedChanged: (Boolean) -> Unit,
    onRatePharmacist: () -> Unit,
    onReportIssue: () -> Unit,
) {
    var expanded by remember(request.requestId) { mutableStateOf(false) }
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = onSelectedChanged,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.medicineName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = request.shopName.ifBlank { "Pharmacy pending" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                OrderStatusBadge(request.status)
            }
            ViewDetailsButton(expanded = expanded, onClick = { expanded = !expanded })
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Quantity ${request.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (request.pharmacistMessage.isNotBlank()) {
                        Text(
                            text = "Pharmacist message: ${request.pharmacistMessage}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (request.issueReported) {
                        IssueReportedBadge()
                    }
                    if (request.status == ArrangementStatus.Completed) {
                        TextButton(
                            onClick = onReportIssue,
                            enabled = !request.issueReported,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(if (request.issueReported) "Issue Reported" else "Report Issue")
                        }
                        Button(
                            onClick = onRatePharmacist,
                            enabled = !request.pharmacistRated,
                            shape = RoundedCornerShape(DashboardRadius),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Rounded.Star, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text(if (request.pharmacistRated) "Rated" else "Rate Pharmacist", maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IssueReportedBadge() {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = EmergencyRed.copy(alpha = 0.14f),
        contentColor = EmergencyRed,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
            Text("Issue Reported", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun RatingDialog(
    title: String,
    targetName: String,
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit,
) {
    var selectedRating by remember { mutableStateOf(5) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = targetName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    (1..5).forEach { rating ->
                        FilterChip(
                            selected = selectedRating == rating,
                            onClick = { selectedRating = rating },
                            label = { Text("$rating/5") },
                            colors = dashboardFilterChipColors(),
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(selectedRating) }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun VillagerLocationCard(
    location: VillagerLocationUiState,
    onLocationChanged: (String, Double?, Double?) -> Unit,
    onClearLocation: () -> Unit,
) {
    val context = LocalContext.current
    var address by remember(location.address) { mutableStateOf(location.address) }
    var latitude by remember(location.latitude) { mutableStateOf(location.latitude) }
    var longitude by remember(location.longitude) { mutableStateOf(location.longitude) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    fun fetchAndSaveLocation(permissionDenied: Boolean = false) {
        statusMessage = "Fetching your live location..."
        fetchNativeLiveLocation(
            context = context,
            fallbackAddress = address,
            onLocationFetched = { liveLocation ->
                latitude = liveLocation.latitude
                longitude = liveLocation.longitude
                onLocationChanged(address, liveLocation.latitude, liveLocation.longitude)
                statusMessage = locationSavedMessage(
                    liveLocation = liveLocation,
                    liveText = "Live location saved privately for range filtering.",
                    approximateText = if (permissionDenied) {
                        "Permission denied. Approximate location saved for range filtering."
                    } else {
                        "Approximate location saved for range filtering."
                    },
                )
            },
            onLocationUnavailable = {
                statusMessage = "No recent live location was available."
            },
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            fetchAndSaveLocation()
        } else {
            fetchAndSaveLocation(permissionDenied = true)
        }
    }

    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Your location",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (location.hasCoordinates) {
                    "Range filters are using your saved location."
                } else {
                    "Fetch live location once to use distance filters. Your coordinates stay hidden."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it.take(160) },
                label = { Text("Full Address / Landmark") },
                singleLine = true,
                colors = dashboardTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        if (hasNativeLocationPermission(context)) {
                            fetchAndSaveLocation()
                        } else {
                            locationPermissionLauncher.launch(NativeLocationPermissions)
                        }
                    },
                    shape = RoundedCornerShape(DashboardRadius),
                ) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Fetch Live Location")
                }
                OutlinedButton(
                    onClick = {
                        onLocationChanged(address, latitude, longitude)
                        statusMessage = "Address saved."
                    },
                    shape = RoundedCornerShape(DashboardRadius),
                ) {
                    Text("Save Address")
                }
                TextButton(
                    onClick = {
                        address = ""
                        latitude = null
                        longitude = null
                        onClearLocation()
                        statusMessage = "Location cleared."
                    },
                ) {
                    Text("Clear")
                }
            }
            statusMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RealtimeMedicinesStateContent(
    state: MedicineUiState,
    title: String,
    filter: (MedicineItem) -> Boolean = { true },
    emptyText: String = "No medicines are available yet.",
    locationRequiredForRadius: Boolean = false,
) {
    when (state) {
        MedicineUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        is MedicineUiState.Error -> {
            Text(
                text = state.message,
                style = MaterialTheme.typography.bodyLarge,
                color = EmergencyRed,
                modifier = Modifier.padding(vertical = 24.dp),
            )
        }
        is MedicineUiState.Success -> {
            RealtimeMedicinesList(
                medicines = state.medicines.filter(filter),
                title = title,
                emptyText = emptyText,
                locationRequiredForRadius = locationRequiredForRadius,
            )
        }
    }
}

@Composable
private fun RealtimeMedicinesList(
    medicines: List<MedicineItem>,
    title: String = "Find Medicines",
    emptyText: String = "No medicines are available yet.",
    locationRequiredForRadius: Boolean = false,
) {
    var query by remember { mutableStateOf("") }
    var availableOnly by remember { mutableStateOf(false) }
    var selectedRadiusKm by remember { mutableStateOf<Int?>(null) }
    val filteredMedicines = medicines.filter { medicine ->
        val matchesQuery = query.isBlank() ||
            medicine.name.contains(query, ignoreCase = true) ||
            medicine.shopName.contains(query, ignoreCase = true)
        val matchesAvailability = !availableOnly || medicine.stockStatus == "Available"
        val matchesRadius = selectedRadiusKm == null ||
            (
                locationRequiredForRadius &&
                    medicine.distanceKm?.let { distance ->
                        if (selectedRadiusKm == 16) distance > 15.0 else distance <= selectedRadiusKm!!
                    } == true
                )
        matchesQuery && matchesAvailability && matchesRadius
    }

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    OutlinedTextField(
        value = query,
        onValueChange = { query = it.take(80) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
            )
        },
        label = { Text("Search medicines or shops") },
        singleLine = true,
        shape = RoundedCornerShape(DashboardRadius),
        colors = dashboardTextFieldColors(),
        modifier = Modifier.fillMaxWidth(),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = availableOnly,
            onClick = { availableOnly = !availableOnly },
            label = { Text("Available Only") },
            colors = dashboardFilterChipColors(),
        )
        listOf(5, 10, 15).forEach { radius ->
            FilterChip(
                selected = selectedRadiusKm == radius,
                onClick = { selectedRadiusKm = if (selectedRadiusKm == radius) null else radius },
                enabled = locationRequiredForRadius,
                label = { Text("$radius km") },
                colors = dashboardFilterChipColors(),
            )
        }
        FilterChip(
            selected = selectedRadiusKm == 16,
            onClick = { selectedRadiusKm = if (selectedRadiusKm == 16) null else 16 },
            enabled = locationRequiredForRadius,
            label = { Text("> 15 km") },
            colors = dashboardFilterChipColors(),
        )
    }
    if (selectedRadiusKm != null && !locationRequiredForRadius) {
        Text(
            text = "Set your location above to apply range filters.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    if (filteredMedicines.isEmpty()) {
        EmptyStateText(emptyText)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            filteredMedicines.forEach { medicine ->
                MedicineItemCard(medicine = medicine)
            }
        }
    }
}

@Composable
private fun MedicineItemCard(medicine: MedicineItem) {
    val context = LocalContext.current
    var expanded by remember(medicine.id) { mutableStateOf(false) }
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = medicine.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (medicine.isLifeSaving) {
                        LifeSavingBadge()
                    }
                }
                ViewDetailsButton(expanded = expanded, onClick = { expanded = !expanded })
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (medicine.shopName.isNotBlank()) {
                        Text(
                            text = medicine.shopName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = if (medicine.requiresPrescription) "Prescription required" else "No prescription required",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Quantity ${medicine.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = medicine.distanceKm?.let { "${String.format(Locale.US, "%.1f", it)} km away" }
                            ?: "Distance unavailable",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    StockStatusBadge(medicine.stockStatus)
                    medicine.expiryDate?.toDate()?.time?.let { expiryMillis ->
                        ExpiryCountdownText(expiryDateMillis = expiryMillis)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MedicineContactBox(
                            icon = Icons.Rounded.Phone,
                            label = medicine.shopPhone.ifBlank { "Phone unavailable" },
                            enabled = medicine.shopPhone.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            onClick = { openPhoneDialer(context, medicine.shopPhone) },
                        )
                        MedicineContactBox(
                            icon = Icons.Rounded.LocationOn,
                            label = medicine.shopAddress
                                .ifBlank { medicine.fullAddress }
                                .ifBlank { "Open location" },
                            enabled = medicine.hasShopLocation(),
                            modifier = Modifier.weight(1f),
                            onClick = { openMedicineInGoogleMaps(context, medicine) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicineContactBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(DashboardRadius),
        color = MaterialTheme.colorScheme.surface,
        contentColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        modifier = modifier
            .heightIn(min = 58.dp)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null)
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RealtimeShopsList(
    shops: List<ShopResult>,
    villagerLocation: VillagerLocationUiState,
    title: String = "Direct Shop Request",
    isGuest: Boolean = false,
    onArrangementRequest: (ShopResult) -> Unit,
) {
    var shopQuery by remember { mutableStateOf("") }
    var selectedDistanceKm by remember { mutableStateOf<Int?>(null) }
    val shopsWithDistance = shops.map { shop -> shop to shop.distanceFrom(villagerLocation) }
    val visibleShops = shopsWithDistance.filter { (shop, distanceKm) ->
        val matchesName = shopQuery.isBlank() ||
            shop.shopName.contains(shopQuery, ignoreCase = true)
        val matchesDistance = selectedDistanceKm == null || distanceKm?.let { it <= selectedDistanceKm!! } == true
        matchesName && matchesDistance
    }

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    OutlinedTextField(
        value = shopQuery,
        onValueChange = { shopQuery = it.take(80) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        label = { Text("Search medical shop name") },
        singleLine = true,
        shape = RoundedCornerShape(DashboardRadius),
        colors = dashboardTextFieldColors(),
        modifier = Modifier.fillMaxWidth(),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedDistanceKm == null,
            onClick = { selectedDistanceKm = null },
            label = { Text("All") },
            colors = dashboardFilterChipColors(),
        )
        listOf(5, 10).forEach { distance ->
            FilterChip(
                selected = selectedDistanceKm == distance,
                onClick = { selectedDistanceKm = distance },
                enabled = villagerLocation.hasCoordinates,
                label = { Text("Within $distance km") },
                colors = dashboardFilterChipColors(),
            )
        }
    }
    if (selectedDistanceKm != null && !villagerLocation.hasCoordinates) {
        Text(
            text = "Fetch and save your location in profile setup to use distance filters.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    if (isGuest) {
        GuestLockedFeatureMessage()
    }
    if (visibleShops.isEmpty()) {
        EmptyStateText("No approved pharmacies are available yet.")
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            visibleShops.forEach { (shop, distanceKm) ->
                ApprovedShopCard(
                    shop = shop,
                    distanceKm = distanceKm,
                    onArrangementRequest = { onArrangementRequest(shop) },
                )
            }
        }
    }
}

@Composable
private fun ApprovedShopCard(
    shop: ShopResult,
    distanceKm: Double?,
    onArrangementRequest: () -> Unit,
) {
    var expanded by remember(shop.uid) { mutableStateOf(false) }
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = shop.shopName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                ViewDetailsButton(expanded = expanded, onClick = { expanded = !expanded })
            }
            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = shop.address.ifBlank { "Address unavailable" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = shop.phone.ifBlank { "Phone unavailable" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = ratingDisplayText(shop.ratingSum, shop.ratingCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    distanceKm?.let {
                        Text(
                            text = "${String.format(Locale.US, "%.1f", it)} km away",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Button(
                        onClick = onArrangementRequest,
                        shape = RoundedCornerShape(DashboardRadius),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Request Medicine Arrangement")
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FirestoreArrangementBottomSheet(
    shop: ShopResult,
    profile: UserProfile?,
    onDismiss: () -> Unit,
    onSubmit: (
        villagerName: String,
        villagerPhone: String,
        villagerAddress: String,
        villagerLatitude: Double?,
        villagerLongitude: Double?,
        medicineName: String,
        quantity: Int,
        prescriptionUri: String?,
    ) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var villagerName by remember(shop.uid, profile?.fullName) { mutableStateOf(profile?.fullName.orEmpty()) }
    var villagerPhone by remember(shop.uid, profile?.phone) { mutableStateOf(profile?.phone.orEmpty()) }
    var villagerAddress by remember(shop.uid, profile?.address) { mutableStateOf(profile?.address.orEmpty()) }
    var villagerLatitude by remember(shop.uid, profile?.latitude) { mutableStateOf(profile?.latitude) }
    var villagerLongitude by remember(shop.uid, profile?.longitude) { mutableStateOf(profile?.longitude) }
    var medicineName by remember(shop.uid) { mutableStateOf("") }
    var quantity by remember(shop.uid) { mutableStateOf(1) }
    var prescriptionUri by remember(shop.uid) { mutableStateOf<Uri?>(null) }
    val canSubmit = villagerName.isNotBlank() &&
        villagerPhone.length == 10 &&
        villagerAddress.isNotBlank() &&
        medicineName.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Medicine Arrangement Request", style = MaterialTheme.typography.titleLarge)
            Text(shop.shopName, style = MaterialTheme.typography.titleMedium)
            Surface(
                shape = RoundedCornerShape(DashboardRadius),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Patient contact", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = villagerName,
                        onValueChange = { villagerName = it.take(60) },
                        label = { Text("Patient / Your Name") },
                        singleLine = true,
                        colors = dashboardTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = villagerPhone,
                        onValueChange = { villagerPhone = it.filter(Char::isDigit).take(10) },
                        label = { Text("Contact Number") },
                        singleLine = true,
                        colors = dashboardTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = villagerAddress,
                        onValueChange = { villagerAddress = it.take(180) },
                        label = { Text("Delivery Address") },
                        minLines = 2,
                        colors = dashboardTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it.take(80) },
                label = { Text("Medicine Name") },
                singleLine = true,
                colors = dashboardTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            QuantityStepper(quantity = quantity, onQuantityChanged = { quantity = it })
            PrescriptionUploadArea(onPrescriptionChanged = { prescriptionUri = it })
            Button(
                onClick = {
                    onSubmit(
                        villagerName,
                        villagerPhone,
                        villagerAddress,
                        villagerLatitude,
                        villagerLongitude,
                        medicineName,
                        quantity,
                        prescriptionUri?.toString(),
                    )
                },
                enabled = canSubmit,
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text("Submit Request")
            }
        }
    }
}

@Composable
private fun EmptyStateText(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PharmacistDashboard(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    viewModel: PharmacistViewModel,
    onLogout: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(PharmacistDashboardTab.Profile) }
    var completionRatingRequest by remember { mutableStateOf<ArrangementRequest?>(null) }
    val requests by viewModel.incomingRequests.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val profile by viewModel.pharmacistProfile.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            DashboardTopBar(
                darkTheme = darkTheme,
                onDarkThemeChanged = onDarkThemeChanged,
                onLogout = onLogout,
            )
        },
        bottomBar = {
            PharmacistNavigationBar(
                selectedTab = selectedTab,
                onSelectedTab = { selectedTab = it },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (selectedTab) {
                PharmacistDashboardTab.Profile -> PharmacistProfileTab(
                    profile = profile,
                    onProfileChanged = viewModel::updatePharmacistProfile,
                )
                PharmacistDashboardTab.Requests -> PharmacistRequestsTab(
                    requests = requests,
                    onAccept = { requestId, message ->
                        viewModel.updateRequestStatus(requestId, ArrangementStatus.Accepted, message)
                    },
                    onReject = { requestId, message ->
                        viewModel.updateRequestStatus(requestId, ArrangementStatus.Rejected, message)
                    },
                    onComplete = { request -> completionRatingRequest = request },
                    onClearSelected = viewModel::clearIncomingRequests,
                    onDelete = viewModel::deleteIncomingRequest,
                )
                PharmacistDashboardTab.Inventory -> PharmacistInventoryTab(
                    inventory = inventory,
                    onStockStatusChanged = viewModel::updateMedicineStockStatus,
                    onAddMedicine = viewModel::addMedicine,
                    onClearSelected = viewModel::clearInventory,
                    onDeleteMedicine = viewModel::deleteMedicine,
                )
                PharmacistDashboardTab.Alerts -> PharmacistEmergencyAlertsTab(
                    alerts = requests.filter { it.status == ArrangementStatus.Pending && it.hasPrescription },
                )
            }
        }
    }

    completionRatingRequest?.let { request ->
        RatingDialog(
            title = "Rate Villager",
            targetName = request.villagerName.ifBlank { "Villager" },
            onDismiss = { completionRatingRequest = null },
            onSubmit = { rating ->
                viewModel.completeRequestWithVillagerRating(request, rating)
                completionRatingRequest = null
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AdminDashboard(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    viewModel: AdminViewModel,
    onLogout: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(AdminDashboardTab.Overview) }
    val overview by viewModel.overview.collectAsState()
    val pendingPharmacies by viewModel.pendingPharmacies.collectAsState()
    val activePharmacies by viewModel.activePharmacies.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()
    val requestClearState by viewModel.requestClearState.collectAsState()
    val requestLogPharmacies = activePharmacies + pendingPharmacies
    val tabs = listOf(
        AdminDashboardTab.Overview to "Overview",
        AdminDashboardTab.VerifyPharmacies to "Verify Pharmacies",
        AdminDashboardTab.ManageShops to "Manage Shops",
        AdminDashboardTab.RequestLogs to "Request Logs",
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            DashboardTopBar(
                darkTheme = darkTheme,
                onDarkThemeChanged = onDarkThemeChanged,
                onLogout = onLogout,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab },
                edgePadding = 8.dp,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                tabs.forEach { (tab, label) ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (selectedTab) {
                    AdminDashboardTab.Overview -> AdminOverviewTab(overview = overview)
                    AdminDashboardTab.VerifyPharmacies -> VerifyPharmaciesTab(
                        pharmacies = pendingPharmacies,
                        onApprove = viewModel::approvePharmacy,
                        onReject = viewModel::rejectPharmacy,
                    )
                    AdminDashboardTab.ManageShops -> ActivePharmaciesTab(
                        pharmacies = activePharmacies,
                        onRemove = viewModel::removePharmacy,
                    )
                    AdminDashboardTab.RequestLogs -> AdminTransactionsTab(
                        requests = allRequests,
                        pharmacies = requestLogPharmacies,
                        clearState = requestClearState,
                        onClearSelected = viewModel::deleteRequests,
                        onClearAll = viewModel::clearAllRequests,
                        onClearMessageDismissed = viewModel::clearRequestClearMessage,
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DashboardTopBar(
    onLogout: () -> Unit,
    darkTheme: Boolean? = null,
    onDarkThemeChanged: ((Boolean) -> Unit)? = null,
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = "Grama-Sanjeevini",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            if (darkTheme != null && onDarkThemeChanged != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Dark mode",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = onDarkThemeChanged,
                    )
                }
            }
            IconButton(onClick = { showLogoutConfirmation = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Logout,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.primary,
        ),
    )

    if (showLogoutConfirmation) {
        ConfirmationDialog(
            title = "Logout",
            text = "Are you sure you want to log out of your account?",
            confirmText = "Yes, Logout",
            onConfirm = {
                showLogoutConfirmation = false
                onLogout()
            },
            onDismiss = { showLogoutConfirmation = false },
        )
    }
}

@Composable
private fun PharmacistNavigationBar(
    selectedTab: PharmacistDashboardTab,
    onSelectedTab: (PharmacistDashboardTab) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        NavigationBarItem(
            selected = selectedTab == PharmacistDashboardTab.Profile,
            onClick = { onSelectedTab(PharmacistDashboardTab.Profile) },
            icon = { Icon(Icons.Rounded.AccountCircle, contentDescription = null) },
            label = { Text("Profile") },
            colors = dashboardNavigationItemColors(),
        )
        NavigationBarItem(
            selected = selectedTab == PharmacistDashboardTab.Requests,
            onClick = { onSelectedTab(PharmacistDashboardTab.Requests) },
            icon = { Icon(Icons.AutoMirrored.Rounded.Assignment, contentDescription = null) },
            label = { Text("Requests") },
            colors = dashboardNavigationItemColors(),
        )
        NavigationBarItem(
            selected = selectedTab == PharmacistDashboardTab.Inventory,
            onClick = { onSelectedTab(PharmacistDashboardTab.Inventory) },
            icon = { Icon(Icons.Rounded.Inventory2, contentDescription = null) },
            label = { Text("My Inventory") },
            colors = dashboardNavigationItemColors(),
        )
        NavigationBarItem(
            selected = selectedTab == PharmacistDashboardTab.Alerts,
            onClick = { onSelectedTab(PharmacistDashboardTab.Alerts) },
            icon = { Icon(Icons.Rounded.NotificationsActive, contentDescription = null) },
            label = { Text("Emergency Alerts") },
            colors = dashboardNavigationItemColors(),
        )
    }
}

@Composable
private fun PharmacistRequestsTab(
    requests: List<ArrangementRequest>,
    onAccept: (String, String) -> Unit,
    onReject: (String, String) -> Unit,
    onComplete: (ArrangementRequest) -> Unit,
    onClearSelected: (Collection<String>) -> Unit,
    onDelete: (String) -> Unit,
) {
    val activeRequests = requests.filter {
        it.status == ArrangementStatus.Pending || it.status == ArrangementStatus.Accepted
    }
    var selectedRequestIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var requestIdsPendingClear by remember { mutableStateOf<Set<String>?>(null) }
    var statusUpdatePending by remember { mutableStateOf<Pair<String, ArrangementStatus>?>(null) }
    Text(
        text = "Incoming Orders",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    if (activeRequests.isEmpty()) {
        EmptyStateText("No incoming requests yet.")
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = {
                    requestIdsPendingClear = selectedRequestIds
                },
                enabled = selectedRequestIds.isNotEmpty(),
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.weight(1f),
            ) {
                Text("Clear selected")
            }
            FilledTonalButton(
                onClick = {
                    requestIdsPendingClear = activeRequests.map { it.requestId }.toSet()
                },
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.weight(1f),
            ) {
                Text("Clear all")
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(activeRequests, key = { it.requestId }) { request ->
                IncomingRequestCard(
                    request = request,
                    selected = request.requestId in selectedRequestIds,
                    onSelectedChanged = { selected ->
                        selectedRequestIds = if (selected) {
                            selectedRequestIds + request.requestId
                        } else {
                            selectedRequestIds - request.requestId
                        }
                    },
                    onAccept = {
                        statusUpdatePending = request.requestId to ArrangementStatus.Accepted
                    },
                    onReject = {
                        statusUpdatePending = request.requestId to ArrangementStatus.Rejected
                    },
                    onComplete = { onComplete(request) },
                    onDelete = { requestIdsPendingClear = setOf(request.requestId) },
                )
            }
        }
    }

    requestIdsPendingClear?.let { requestIds ->
        ConfirmationDialog(
            title = "Clear Logs",
            text = "Are you sure you want to remove these logs from your view?",
            confirmText = "Clear",
            onConfirm = {
                if (requestIds.size == 1) {
                    onDelete(requestIds.first())
                } else {
                    onClearSelected(requestIds)
                }
                selectedRequestIds = emptySet()
                requestIdsPendingClear = null
            },
            onDismiss = { requestIdsPendingClear = null },
            isDestructive = true,
        )
    }

    statusUpdatePending?.let { (requestId, status) ->
        PharmacistRequestMessageDialog(
            status = status,
            onConfirm = { message ->
                if (status == ArrangementStatus.Accepted) {
                    onAccept(requestId, message)
                } else {
                    onReject(requestId, message)
                }
                statusUpdatePending = null
            },
            onDismiss = { statusUpdatePending = null },
        )
    }
}

@Composable
private fun PharmacistRequestMessageDialog(
    status: ArrangementStatus,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var message by remember(status) { mutableStateOf("") }
    val isRejecting = status == ArrangementStatus.Rejected
    val action = if (isRejecting) "Reject" else "Accept"
    val hint = if (isRejecting) {
        "Send message (in 60 characters) (eg. reason of why order got rejected)"
    } else {
        "Send message (in 60 characters) (eg. Order available in 20 min, etc)"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$action Request") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Add a short message for the villager.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it.take(60) },
                    placeholder = { Text(hint) },
                    supportingText = {
                        Text(
                            text = "${message.length}/60",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    minLines = 2,
                    maxLines = 3,
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(message) }) {
                Text(
                    text = "Yes, $action",
                    color = if (isRejecting) EmergencyRed else MaterialTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun PharmacistProfileTab(
    profile: PharmacistProfile?,
    onProfileChanged: (String, String, String, String, Double?, Double?, String, String, String, String, Boolean, String) -> Unit,
) {
    var showEditDialog by remember { mutableStateOf(false) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ElevatedCard(
                shape = RoundedCornerShape(DashboardRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile?.shopName?.takeIf { it.isNotBlank() } ?: "Pharmacy Profile",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Your profile",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp),
                        )
                    }
                    ProfileInfoRow(label = "Owner", value = profile?.ownerName.orEmpty().ifBlank { "Unavailable" })
                    ProfileInfoRow(label = "Phone", value = profile?.phone.orEmpty().ifBlank { "Unavailable" })
                    ProfileInfoRow(label = "Email", value = profile?.email.orEmpty().ifBlank { "Unavailable" })
                    ProfileInfoRow(
                        label = "Drug License",
                        value = profile?.drugLicenseNumber.orEmpty().ifBlank { "Unavailable" },
                    )
                    ProfileInfoRow(
                        label = "Pharmacist Reg. No.",
                        value = profile?.pharmacistRegNumber.orEmpty().ifBlank { "Unavailable" },
                    )
                    ProfileInfoRow(label = "GSTIN", value = profile?.gstin.orEmpty().ifBlank { "Not added" })
                    ProfileInfoRow(label = "Shop Timings", value = profile?.shopTimings.orEmpty().ifBlank { "Not added" })
                    ProfileInfoRow(
                        label = "FSSAI",
                        value = if (profile?.hasFssai == true) {
                            profile.fssaiNumber.ifBlank { "Required but not added" }
                        } else {
                            "Not applicable"
                        },
                    )
                    ProfileInfoRow(
                        label = "Overall Rating",
                        value = ratingDisplayText(profile?.ratingSum ?: 0.0, profile?.ratingCount ?: 0),
                    )
                    Button(
                        onClick = { showEditDialog = true },
                        enabled = profile != null,
                        shape = RoundedCornerShape(DashboardRadius),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Edit your Details")
                    }
                }
            }
        }
    }

    if (showEditDialog && profile != null) {
        EditPharmacistDetailsDialog(
            profile = profile,
            onDismiss = { showEditDialog = false },
            onSave = { shopName, ownerName, phone, address, latitude, longitude, drugLicenseNumber, pharmacistRegNumber, gstin, shopTimings, hasFssai, fssaiNumber ->
                onProfileChanged(
                    shopName,
                    ownerName,
                    phone,
                    address,
                    latitude,
                    longitude,
                    drugLicenseNumber,
                    pharmacistRegNumber,
                    gstin,
                    shopTimings,
                    hasFssai,
                    fssaiNumber,
                )
                showEditDialog = false
            },
        )
    }
}

@Composable
private fun EditPharmacistDetailsDialog(
    profile: PharmacistProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Double?, Double?, String, String, String, String, Boolean, String) -> Unit,
) {
    val context = LocalContext.current
    var shopName by remember(profile.uid) { mutableStateOf(profile.shopName) }
    var ownerName by remember(profile.uid) { mutableStateOf(profile.ownerName) }
    var phone by remember(profile.uid) { mutableStateOf(profile.phone) }
    var drugLicenseNumber by remember(profile.uid) { mutableStateOf(profile.drugLicenseNumber) }
    var pharmacistRegNumber by remember(profile.uid) { mutableStateOf(profile.pharmacistRegNumber) }
    var gstin by remember(profile.uid) { mutableStateOf(profile.gstin) }
    var shopTimings by remember(profile.uid) { mutableStateOf(profile.shopTimings) }
    var hasFssai by remember(profile.uid) { mutableStateOf(profile.hasFssai) }
    var fssaiNumber by remember(profile.uid) { mutableStateOf(profile.fssaiNumber) }
    var address by remember(profile.uid) { mutableStateOf(profile.address) }
    var latitude by remember(profile.uid) { mutableStateOf(profile.latitude) }
    var longitude by remember(profile.uid) { mutableStateOf(profile.longitude) }
    var statusMessage by remember(profile.uid) { mutableStateOf<String?>(null) }
    val canSave = shopName.trim().length >= 2 &&
        ownerName.trim().length >= 2 &&
        phone.length == 10 &&
        drugLicenseNumber.trim().isNotBlank() &&
        pharmacistRegNumber.trim().isNotBlank() &&
        (!hasFssai || fssaiNumber.trim().isNotBlank()) &&
        address.trim().isNotBlank()
    fun fetchAndSaveLocation(permissionDenied: Boolean = false) {
        statusMessage = "Fetching your live location..."
        fetchNativeLiveLocation(
            context = context,
            fallbackAddress = address,
            onLocationFetched = { liveLocation ->
                latitude = liveLocation.latitude
                longitude = liveLocation.longitude
                statusMessage = locationSavedMessage(
                    liveLocation = liveLocation,
                    liveText = "Live location saved privately.",
                    approximateText = if (permissionDenied) {
                        "Permission denied. Approximate location saved privately."
                    } else {
                        "Approximate location saved privately."
                    },
                )
            },
            onLocationUnavailable = {
                statusMessage = "No live location was available. Try again near an open map signal."
            },
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            fetchAndSaveLocation()
        } else {
            fetchAndSaveLocation(permissionDenied = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit your Details") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it.take(80) },
                    label = { Text("Shop Name") },
                    singleLine = true,
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it.take(60) },
                    label = { Text("Owner Name") },
                    singleLine = true,
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.filter(Char::isDigit).take(10) },
                    label = { Text("Phone") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = drugLicenseNumber,
                    onValueChange = { drugLicenseNumber = it.uppercase().take(120) },
                    label = { Text("Drug License Number (Form 20/21)") },
                    singleLine = false,
                    minLines = 2,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = pharmacistRegNumber,
                    onValueChange = { pharmacistRegNumber = it.uppercase().take(40) },
                    label = { Text("Registered Pharmacist Number (State Council ID)") },
                    singleLine = true,
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = gstin,
                    onValueChange = { gstin = it.uppercase().take(15) },
                    label = { Text("GSTIN (Optional)") },
                    singleLine = true,
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = shopTimings,
                    onValueChange = { shopTimings = it.take(80) },
                    label = { Text("Shop Timings (e.g., 9 AM - 10 PM)") },
                    singleLine = true,
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Do you sell health supplements, baby food, or nutritional products?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = hasFssai,
                        onCheckedChange = { checked ->
                            hasFssai = checked
                            if (!checked) fssaiNumber = ""
                        },
                    )
                }
                if (hasFssai) {
                    OutlinedTextField(
                        value = fssaiNumber,
                        onValueChange = { fssaiNumber = it.filter(Char::isDigit).take(14) },
                        label = { Text("FSSAI License Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = dashboardTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it.take(180) },
                    label = { Text("Full Address / Landmark") },
                    minLines = 2,
                    colors = dashboardTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(
                    onClick = {
                        if (hasNativeLocationPermission(context)) {
                            fetchAndSaveLocation()
                        } else {
                            locationPermissionLauncher.launch(NativeLocationPermissions)
                        }
                    },
                    shape = RoundedCornerShape(DashboardRadius),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Fetch Live Location")
                }
                statusMessage?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        shopName,
                        ownerName,
                        phone,
                        address,
                        latitude,
                        longitude,
                        drugLicenseNumber,
                        pharmacistRegNumber,
                        gstin,
                        shopTimings,
                        hasFssai,
                        if (hasFssai) fssaiNumber else "",
                    )
                },
                enabled = canSave,
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun IncomingRequestCard(
    request: ArrangementRequest,
    selected: Boolean,
    onSelectedChanged: (Boolean) -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
) {
    val context = LocalContext.current
    val prescriptionTarget = request.prescriptionUrl ?: request.prescriptionDocumentUri
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = onSelectedChanged,
                )
                Text(
                    text = request.villagerName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onDelete) {
                    Text("Clear")
                }
            }
            Text(
                text = request.medicineName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Quantity ${request.quantity} | ${request.status.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Phone: ${request.villagerPhone.ifBlank { "Unavailable" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Address: ${request.villagerAddress.ifBlank { "Unavailable" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilledTonalButton(
                    onClick = { openPhoneDialer(context, request.villagerPhone) },
                    enabled = request.villagerPhone.isNotBlank(),
                    shape = RoundedCornerShape(DashboardRadius),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Rounded.Phone, contentDescription = "Call villager")
                    Spacer(Modifier.size(8.dp))
                    Text("Phone", maxLines = 1)
                }
                FilledTonalButton(
                    onClick = { openRequestLocationInMaps(context, request) },
                    enabled = request.hasVillagerLocation(),
                    shape = RoundedCornerShape(DashboardRadius),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = "Open villager location")
                    Spacer(Modifier.size(8.dp))
                    Text("Map", maxLines = 1)
                }
            }
            Text(
                text = "Villager rating: ${ratingDisplayText(request.villagerRatingSum, request.villagerRatingCount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (request.issueReported) {
                IssueReportedBadge()
            }
            OutlinedButton(
                onClick = {
                    if (!request.hasPrescription || prescriptionTarget.isNullOrBlank()) {
                        Toast.makeText(context, "Prescription not available", Toast.LENGTH_SHORT).show()
                    } else {
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(prescriptionTarget)))
                        }.onFailure {
                            Toast.makeText(context, "Prescription not available", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Rounded.UploadFile, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("View Prescription")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                when (request.status) {
                    ArrangementStatus.Pending -> {
                        FilledTonalButton(
                            onClick = onAccept,
                            shape = RoundedCornerShape(DashboardRadius),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = AvailabilityGreen.copy(alpha = 0.16f),
                                contentColor = AvailabilityGreen,
                            ),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Accept", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        FilledTonalButton(
                            onClick = onReject,
                            shape = RoundedCornerShape(DashboardRadius),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = EmergencyRed.copy(alpha = 0.14f),
                                contentColor = EmergencyRed,
                            ),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Reject", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    ArrangementStatus.Accepted -> {
                        Button(
                            onClick = onComplete,
                            shape = RoundedCornerShape(DashboardRadius),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Mark as Completed")
                        }
                    }
                    ArrangementStatus.Completed,
                    ArrangementStatus.Rejected -> Unit
                }
            }
        }
    }
}

@Composable
private fun PharmacistInventoryTab(
    inventory: List<MedicineItem>,
    onStockStatusChanged: (String, String) -> Unit,
    onAddMedicine: (String, Int, String, Boolean, Boolean, Long?) -> Unit,
    onClearSelected: (Collection<String>) -> Unit,
    onDeleteMedicine: (String) -> Unit,
) {
    var newMedicineName by remember { mutableStateOf("") }
    var newMedicineQuantity by remember { mutableStateOf("") }
    var newMedicineStockStatus by remember { mutableStateOf("Available") }
    var newMedicineRequiresPrescription by remember { mutableStateOf(false) }
    var newMedicineIsLifeSaving by remember { mutableStateOf(false) }
    var newMedicineExpiryDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedMedicineIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var medicineIdsPendingClear by remember { mutableStateOf<Set<String>?>(null) }

    Text(
        text = "My Inventory",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            AddMedicineInventoryCard(
                medicineName = newMedicineName,
                onMedicineNameChanged = { newMedicineName = it },
                quantity = newMedicineQuantity,
                onQuantityChanged = { newMedicineQuantity = it },
                stockStatus = newMedicineStockStatus,
                onStockStatusChanged = { newMedicineStockStatus = it },
                requiresPrescription = newMedicineRequiresPrescription,
                onRequiresPrescriptionChanged = { newMedicineRequiresPrescription = it },
                isLifeSaving = newMedicineIsLifeSaving,
                onIsLifeSavingChanged = { newMedicineIsLifeSaving = it },
                expiryDateMillis = newMedicineExpiryDateMillis,
                onExpiryDateChanged = { newMedicineExpiryDateMillis = it },
                onAddMedicine = {
                    onAddMedicine(
                        newMedicineName,
                        newMedicineQuantity.toIntOrNull() ?: 0,
                        newMedicineStockStatus,
                        newMedicineRequiresPrescription,
                        newMedicineIsLifeSaving,
                        newMedicineExpiryDateMillis,
                    )
                    newMedicineName = ""
                    newMedicineQuantity = ""
                    newMedicineStockStatus = "Available"
                    newMedicineRequiresPrescription = false
                    newMedicineIsLifeSaving = false
                    newMedicineExpiryDateMillis = null
                },
            )
        }
        if (inventory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            medicineIdsPendingClear = selectedMedicineIds
                        },
                        enabled = selectedMedicineIds.isNotEmpty(),
                        shape = RoundedCornerShape(DashboardRadius),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Clear selected")
                    }
                    FilledTonalButton(
                        onClick = {
                            medicineIdsPendingClear = inventory.map { it.medicineId }.toSet()
                        },
                        shape = RoundedCornerShape(DashboardRadius),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Clear all")
                    }
                }
            }
        }
        items(inventory, key = { it.medicineId }) { medicine ->
            InventoryAvailabilityCard(
                medicine = medicine,
                selected = medicine.medicineId in selectedMedicineIds,
                onSelectedChanged = { selected ->
                    selectedMedicineIds = if (selected) {
                        selectedMedicineIds + medicine.medicineId
                    } else {
                        selectedMedicineIds - medicine.medicineId
                    }
                },
                onStockStatusChanged = { stockStatus -> onStockStatusChanged(medicine.medicineId, stockStatus) },
                onDelete = { medicineIdsPendingClear = setOf(medicine.medicineId) },
            )
        }
    }

    medicineIdsPendingClear?.let { medicineIds ->
        ConfirmationDialog(
            title = "Clear Inventory",
            text = "Are you sure you want to remove these medicines from your inventory view?",
            confirmText = "Clear",
            onConfirm = {
                if (medicineIds.size == 1) {
                    onDeleteMedicine(medicineIds.first())
                } else {
                    onClearSelected(medicineIds)
                }
                selectedMedicineIds = emptySet()
                medicineIdsPendingClear = null
            },
            onDismiss = { medicineIdsPendingClear = null },
            isDestructive = true,
        )
    }
}

@Composable
private fun PharmacistShopLocationCard(
    location: ShopLocationUiState,
    onLocationChanged: (String, Double?, Double?) -> Unit,
) {
    val context = LocalContext.current
    var address by remember(location.address) { mutableStateOf(location.address) }
    var latitude by remember(location.latitude) { mutableStateOf(location.latitude) }
    var longitude by remember(location.longitude) { mutableStateOf(location.longitude) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    fun fetchAndSaveLocation(permissionDenied: Boolean = false) {
        statusMessage = "Fetching your live location..."
        fetchNativeLiveLocation(
            context = context,
            fallbackAddress = address,
            onLocationFetched = { liveLocation ->
                latitude = liveLocation.latitude
                longitude = liveLocation.longitude
                onLocationChanged(address, liveLocation.latitude, liveLocation.longitude)
                statusMessage = locationSavedMessage(
                    liveLocation = liveLocation,
                    liveText = "Shop live location saved privately. Existing medicines were updated.",
                    approximateText = if (permissionDenied) {
                        "Permission denied. Approximate shop location saved. Existing medicines were updated."
                    } else {
                        "Approximate shop location saved. Existing medicines were updated."
                    },
                )
            },
            onLocationUnavailable = {
                statusMessage = "No recent live location was available."
            },
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            fetchAndSaveLocation()
        } else {
            fetchAndSaveLocation(permissionDenied = true)
        }
    }

    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Shop location",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (location.hasCoordinates) {
                    "Villagers can calculate distance to your shop."
                } else {
                    "Fetch live location once so villagers can filter your shop by distance."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it.take(160) },
                label = { Text("Full Address / Landmark") },
                singleLine = false,
                minLines = 2,
                colors = dashboardTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        if (hasNativeLocationPermission(context)) {
                            fetchAndSaveLocation()
                        } else {
                            locationPermissionLauncher.launch(NativeLocationPermissions)
                        }
                    },
                    shape = RoundedCornerShape(DashboardRadius),
                ) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Fetch Live Location")
                }
                OutlinedButton(
                    onClick = {
                        onLocationChanged(
                            address,
                            latitude,
                            longitude,
                        )
                        statusMessage = "Shop address saved. Existing medicines were updated."
                    },
                    shape = RoundedCornerShape(DashboardRadius),
                ) {
                    Text("Save Address")
                }
            }
            statusMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AddMedicineInventoryCard(
    medicineName: String,
    onMedicineNameChanged: (String) -> Unit,
    quantity: String,
    onQuantityChanged: (String) -> Unit,
    stockStatus: String,
    onStockStatusChanged: (String) -> Unit,
    requiresPrescription: Boolean,
    onRequiresPrescriptionChanged: (Boolean) -> Unit,
    isLifeSaving: Boolean,
    onIsLifeSavingChanged: (Boolean) -> Unit,
    expiryDateMillis: Long?,
    onExpiryDateChanged: (Long?) -> Unit,
    onAddMedicine: () -> Unit,
) {
    var showExpiryPicker by remember { mutableStateOf(false) }
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Add new medicine",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                value = medicineName,
                onValueChange = { onMedicineNameChanged(it.take(80)) },
                label = { Text("Medicine Name") },
                singleLine = true,
                colors = dashboardTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = quantity,
                onValueChange = { onQuantityChanged(it.filter(Char::isDigit).take(5)) },
                label = { Text("Quantity") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = dashboardTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Stock status",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            StockStatusSelector(
                selectedStatus = stockStatus,
                onStatusSelected = onStockStatusChanged,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Requires Doctor's Prescription",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Mark medicines that must be dispensed only with a prescription.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = requiresPrescription,
                    onCheckedChange = onRequiresPrescriptionChanged,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Life Saving",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Highlight this stock for emergency medicine searches.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = isLifeSaving,
                    onCheckedChange = onIsLifeSavingChanged,
                )
            }
            OutlinedButton(
                onClick = { showExpiryPicker = true },
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = expiryDateMillis?.let { "Expiry: ${formatDateMillis(it)}" }
                        ?: "Select Expiry Date",
                )
            }
            Button(
                onClick = onAddMedicine,
                enabled = medicineName.isNotBlank() && quantity.toIntOrNull() != null,
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Add Medicine")
            }
        }
    }

    if (showExpiryPicker) {
        MedicineExpiryDatePickerDialog(
            selectedDateMillis = expiryDateMillis,
            onDateSelected = onExpiryDateChanged,
            onDismiss = { showExpiryPicker = false },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MedicineExpiryDatePickerDialog(
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                },
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun InventoryAvailabilityCard(
    medicine: MedicineItem,
    selected: Boolean,
    onSelectedChanged: (Boolean) -> Unit,
    onStockStatusChanged: (String) -> Unit,
    onDelete: () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = onSelectedChanged,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = medicine.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = if (medicine.requiresPrescription) {
                            "Requires Prescription"
                        } else {
                            "No Prescription Required"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (medicine.requiresPrescription) {
                            EmergencyRed
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                    if (medicine.shopName.isNotBlank()) {
                        Text(
                            text = medicine.shopName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (medicine.isLifeSaving) {
                        LifeSavingBadge()
                    }
                }
                Text(
                    text = "Qty: ${medicine.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            medicine.expiryDate?.toDate()?.time?.let { expiryMillis ->
                Spacer(modifier = Modifier.height(8.dp))
                ExpiryCountdownText(expiryDateMillis = expiryMillis)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("Available", "Low Stock", "Out of Stock").forEach { status ->
                    FilterChip(
                        selected = medicine.stockStatus == status,
                        onClick = { onStockStatusChanged(status) },
                        label = { Text(status) },
                        colors = dashboardFilterChipColors(),
                    )
                }
                TextButton(onClick = onDelete) {
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
private fun StockStatusSelector(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
) {
    val statuses = listOf("Available", "Low Stock", "Out of Stock")
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        statuses.forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                label = { Text(status) },
                colors = dashboardFilterChipColors(),
            )
        }
    }
}

@Composable
private fun PharmacistEmergencyAlertsTab(alerts: List<ArrangementRequest>) {
    Surface(
        shape = RoundedCornerShape(DashboardRadius),
        color = EmergencyRed.copy(alpha = 0.10f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.NotificationsActive,
                contentDescription = null,
                tint = EmergencyRed,
            )
            Text(
                text = "High-priority SOS feed",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }

    if (alerts.isEmpty()) {
        EmptyStateText("No emergency alerts right now.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(alerts, key = { it.requestId }) { alert ->
                EmergencyAlertCard(alert = alert)
            }
        }
    }
}

@Composable
private fun EmergencyAlertCard(alert: ArrangementRequest) {
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(
            containerColor = EmergencyRed.copy(alpha = 0.10f),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = alert.medicineName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${alert.villagerName} - ${alert.villagerPhone}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Quantity ${alert.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AdminOverviewTab(overview: com.mindmatrix.gramasanjeevini.auth.ui.AdminOverviewUiState) {
    val stats = listOf(
        "Active Pharmacies" to overview.activePharmacies.toString(),
        "Villagers Registered" to overview.villagersRegistered.toString(),
        "Total Requests" to overview.totalRequests.toString(),
        "Most Requested Medicine" to "Live soon",
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(stats.chunked(2)) { rowStats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowStats.forEach { (label, value) ->
                    AdminStatCard(label = label, value = value, modifier = Modifier.weight(1f))
                }
                if (rowStats.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        item {
            ElevatedCard(
                shape = RoundedCornerShape(DashboardRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Request Trend",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Chart placeholder",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTransactionsTab(
    requests: List<ArrangementRequest>,
    pharmacies: List<PharmacistProfile>,
    clearState: AdminRequestClearUiState,
    onClearSelected: (Collection<String>) -> Unit,
    onClearAll: () -> Unit,
    onClearMessageDismissed: () -> Unit,
) {
    var selectedRequestIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var requestIdsPendingClear by remember { mutableStateOf<Set<String>?>(null) }
    var shopSearchQuery by remember { mutableStateOf("") }
    val filteredRequests = requests.filter { request ->
        val pharmacyName = request.shopName.ifBlank {
            pharmacies.firstOrNull { it.uid == request.shopId }?.shopName.orEmpty()
        }
        shopSearchQuery.isBlank() || pharmacyName.contains(shopSearchQuery, ignoreCase = true)
    }
    Text(
        text = "Request Logs",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    OutlinedTextField(
        value = shopSearchQuery,
        onValueChange = { shopSearchQuery = it.take(80) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        label = { Text("Search medical shop name") },
        singleLine = true,
        shape = RoundedCornerShape(DashboardRadius),
        colors = dashboardTextFieldColors(),
        modifier = Modifier.fillMaxWidth(),
    )
    clearState.message?.let { message ->
        Surface(
            shape = RoundedCornerShape(DashboardRadius),
            color = if (clearState.isError) {
                EmergencyRed.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
            },
            contentColor = if (clearState.isError) EmergencyRed else MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onClearMessageDismissed) {
                    Text("Dismiss")
                }
            }
        }
    }
    if (filteredRequests.isEmpty()) {
        EmptyStateText("No medicine arrangement requests yet.")
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = {
                    requestIdsPendingClear = selectedRequestIds
                },
                enabled = selectedRequestIds.isNotEmpty() && !clearState.isClearing,
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.weight(1f),
            ) {
                Text(if (clearState.isClearing) "Clearing..." else "Clear selected")
            }
            FilledTonalButton(
                onClick = {
                    requestIdsPendingClear = requests.map { it.requestId }.toSet()
                },
                enabled = !clearState.isClearing,
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.weight(1f),
            ) {
                Text(if (clearState.isClearing) "Clearing..." else "Clear all")
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredRequests, key = { it.requestId }) { request ->
                val pharmacy = pharmacies.firstOrNull { it.uid == request.shopId }
                AdminTransactionCard(
                    request = request,
                    pharmacy = pharmacy,
                    selected = request.requestId in selectedRequestIds,
                    onSelectedChanged = { selected ->
                        selectedRequestIds = if (selected) {
                            selectedRequestIds + request.requestId
                        } else {
                            selectedRequestIds - request.requestId
                        }
                    },
                )
            }
        }
    }

    requestIdsPendingClear?.let { requestIds ->
        ConfirmationDialog(
            title = "Clear Logs",
            text = "Are you sure you want to remove these logs from your view?",
            confirmText = "Clear",
            onConfirm = {
                if (requestIds.size == requests.size) {
                    onClearAll()
                } else {
                    onClearSelected(requestIds)
                }
                selectedRequestIds = emptySet()
                requestIdsPendingClear = null
            },
            onDismiss = { requestIdsPendingClear = null },
            isDestructive = true,
        )
    }
}

@Composable
private fun AdminTransactionCard(
    request: ArrangementRequest,
    pharmacy: PharmacistProfile?,
    selected: Boolean,
    onSelectedChanged: (Boolean) -> Unit,
) {
    var expanded by remember(request.requestId) { mutableStateOf(false) }
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = onSelectedChanged,
                )
                Column(modifier = Modifier.weight(1f)) {
                    val displayShopName = request.shopName.ifBlank { pharmacy?.shopName ?: request.shopId ?: "Unassigned shop" }
                    Text(
                        text = displayShopName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = request.medicineName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                OrderStatusBadge(status = request.status)
            }
            ViewDetailsButton(expanded = expanded, onClick = { expanded = !expanded })
            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Villager: ${request.villagerName.ifBlank { "Guest villager" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Shop: ${request.shopName.ifBlank { pharmacy?.shopName ?: request.shopId ?: "Unassigned" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Owner: ${pharmacy?.ownerName?.takeIf { it.isNotBlank() } ?: "Unavailable"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Phone: ${pharmacy?.phone?.takeIf { it.isNotBlank() } ?: "Unavailable"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Address: ${pharmacy?.address?.takeIf { it.isNotBlank() } ?: "Unavailable"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Villager phone: ${request.villagerPhone.ifBlank { "Unavailable" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Villager address: ${request.villagerAddress.ifBlank { "Unavailable" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Quantity ${request.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Pharmacist message: ${request.pharmacistMessage.ifBlank { "No message sent" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (request.issueReported) {
                        IssueReportedBadge()
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderStatusBadge(status: ArrangementStatus) {
    val badgeColor = when (status) {
        ArrangementStatus.Pending -> MaterialTheme.colorScheme.primary
        ArrangementStatus.Accepted -> AvailabilityGreen
        ArrangementStatus.Completed -> AvailabilityGreen
        ArrangementStatus.Rejected -> EmergencyRed
    }
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = badgeColor.copy(alpha = 0.14f),
        contentColor = badgeColor,
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun ActivePharmaciesTab(
    pharmacies: List<PharmacistProfile>,
    onRemove: (String) -> Unit,
) {
    var pharmacyPendingRemoval by remember { mutableStateOf<PharmacistProfile?>(null) }

    Text(
        text = "Active Pharmacies",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    if (pharmacies.isEmpty()) {
        EmptyStateText("No active pharmacies are available.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(pharmacies, key = { it.uid }) { pharmacy ->
                ActivePharmacyCard(
                    pharmacy = pharmacy,
                    onRemove = { pharmacyPendingRemoval = pharmacy },
                )
            }
        }
    }

    pharmacyPendingRemoval?.let { pharmacy ->
        ConfirmationDialog(
            title = "Remove Pharmacy",
            text = "Are you sure you want to permanently remove this pharmacy from the active list?",
            confirmText = "Remove",
            onConfirm = {
                onRemove(pharmacy.uid)
                pharmacyPendingRemoval = null
            },
            onDismiss = { pharmacyPendingRemoval = null },
            isDestructive = true,
        )
    }
}

@Composable
private fun ActivePharmacyCard(
    pharmacy: PharmacistProfile,
    onRemove: () -> Unit,
) {
    val context = LocalContext.current
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = pharmacy.shopName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Owner: ${pharmacy.ownerName}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            PharmacyComplianceDetails(pharmacy)
            Text(
                text = "Phone: ${pharmacy.phone.ifBlank { "Unavailable" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Address: ${pharmacy.address.ifBlank { "Unavailable" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilledTonalButton(
                    onClick = { openPhoneDialer(context, pharmacy.phone) },
                    enabled = pharmacy.phone.isNotBlank(),
                    shape = RoundedCornerShape(DashboardRadius),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Rounded.Phone, contentDescription = "Call pharmacy")
                    Spacer(Modifier.size(8.dp))
                    Text("Phone", maxLines = 1)
                }
                FilledTonalButton(
                    onClick = { openPharmacyInGoogleMaps(context, pharmacy) },
                    enabled = pharmacy.hasLocation(),
                    shape = RoundedCornerShape(DashboardRadius),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Rounded.LocationOn, contentDescription = "Open pharmacy location")
                    Spacer(Modifier.size(8.dp))
                    Text("Map", maxLines = 1)
                }
            }
            OutlinedButton(
                onClick = onRemove,
                shape = RoundedCornerShape(DashboardRadius),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed),
                border = BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.72f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Remove Shop")
            }
        }
    }
}

@Composable
private fun PharmacyComplianceDetails(pharmacy: PharmacistProfile) {
    Text(
        text = "Drug License: ${pharmacy.drugLicenseNumber.ifBlank { "Unavailable" }}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
        text = "Pharmacist ID: ${pharmacy.pharmacistRegNumber.ifBlank { "Unavailable" }}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
        text = "GSTIN: ${pharmacy.gstin.ifBlank { "Not added" }}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
        text = "Timings: ${pharmacy.shopTimings.ifBlank { "Not added" }}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
        text = if (pharmacy.hasFssai) {
            "FSSAI: ${pharmacy.fssaiNumber.ifBlank { "Required but not added" }}"
        } else {
            "FSSAI: Not applicable"
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun AdminStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier.heightIn(min = 118.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun VerifyPharmaciesTab(
    pharmacies: List<PharmacistProfile>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
) {
    var pharmacyVerificationPending by remember { mutableStateOf<Pair<PharmacistProfile, Boolean>?>(null) }

    Text(
        text = "Pending Verification",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    if (pharmacies.isEmpty()) {
        EmptyStateText("No pharmacies are waiting for approval.")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(pharmacies, key = { it.uid }) { pharmacy ->
                PendingPharmacyCard(
                    pharmacy = pharmacy,
                    onApprove = { pharmacyVerificationPending = pharmacy to true },
                    onReject = { pharmacyVerificationPending = pharmacy to false },
                )
            }
        }
    }

    pharmacyVerificationPending?.let { (pharmacy, isApproval) ->
        val action = if (isApproval) "Accept" else "Reject"
        ConfirmationDialog(
            title = "Verify Pharmacy",
            text = "Are you sure you want to $action this pharmacy application?",
            confirmText = "Yes, $action",
            onConfirm = {
                if (isApproval) {
                    onApprove(pharmacy.uid)
                } else {
                    onReject(pharmacy.uid)
                }
                pharmacyVerificationPending = null
            },
            onDismiss = { pharmacyVerificationPending = null },
            isDestructive = !isApproval,
        )
    }
}

@Composable
private fun PendingPharmacyCard(
    pharmacy: PharmacistProfile,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = pharmacy.shopName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Owner: ${pharmacy.ownerName}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            PharmacyComplianceDetails(pharmacy)
            Text(
                text = "Phone: ${pharmacy.phone.ifBlank { "Unavailable" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Address: ${pharmacy.address.ifBlank { "Unavailable" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                onClick = onApprove,
                    shape = RoundedCornerShape(DashboardRadius),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Approve Pharmacy", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OutlinedButton(
                    onClick = onReject,
                    shape = RoundedCornerShape(DashboardRadius),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed),
                    border = BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.65f)),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Reject/Ban", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MedicineDashboard(
    title: String,
    subtitle: String,
    showStockTab: Boolean,
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onLogout: () -> Unit,
    viewModel: InventoryViewModel,
) {
    var selectedRequestResult by remember { mutableStateOf<ShopSearchResult?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            },
            bottomBar = {
                DashboardNavigationBar(
                    selected = viewModel.selectedSection,
                    showStockTab = showStockTab,
                    onSelected = viewModel::showSection,
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
            ) {
                viewModel.requestStatusMessage?.let { message ->
                    RequestStatusCard(message = message, onDismiss = viewModel::clearRequestStatus)
                    Spacer(Modifier.height(12.dp))
                }

                when (viewModel.selectedSection) {
                    DashboardSection.Home -> EmptyStateText("Open Search to find medicines or request a shop directly.")
                    DashboardSection.Search -> SearchScreen(
                        viewModel = viewModel,
                        onArrangementRequest = { selectedRequestResult = it },
                    )
                    DashboardSection.Emergency -> EmergencyScreen(
                        results = viewModel.emergencyResults,
                    )
                    DashboardSection.Stock -> PharmacistStockScreen(viewModel)
                }
            }
        }
    }

    selectedRequestResult?.let { result ->
        ArrangementRequestDialog(
            result = result,
            onDismiss = { selectedRequestResult = null },
            onSubmit = { villagerName, villagerPhone, medicineName, quantity, prescriptionUri ->
                if (
                    viewModel.submitShopArrangementRequest(
                        result = result,
                        villagerName = villagerName,
                        villagerPhone = villagerPhone,
                        medicineName = medicineName,
                        requestedQuantity = quantity,
                        prescriptionDocumentUri = prescriptionUri,
                    )
                ) {
                    selectedRequestResult = null
                }
            },
        )
    }

    if (showSettingsDialog) {
        DashboardSettingsDialog(
            darkTheme = darkTheme,
            onDarkThemeChanged = onDarkThemeChanged,
            roleActionText = if (showStockTab) "Logout" else "Back to roles",
            onRoleAction = {
                showSettingsDialog = false
                if (showStockTab) {
                    showLogoutConfirmation = true
                } else {
                    onLogout()
                }
            },
            onDismiss = { showSettingsDialog = false },
        )
    }

    if (showLogoutConfirmation) {
        ConfirmationDialog(
            title = "Logout",
            text = "Are you sure you want to log out of your account?",
            confirmText = "Yes, Logout",
            onConfirm = {
                showLogoutConfirmation = false
                onLogout()
            },
            onDismiss = { showLogoutConfirmation = false },
        )
    }
}

@Composable
private fun SearchScreen(
    viewModel: InventoryViewModel,
    onArrangementRequest: (ShopSearchResult) -> Unit,
) {
    SearchRequestTabRow(
        selectedTab = viewModel.selectedSearchTab,
        onSelectedTab = viewModel::showSearchTab,
    )
    Spacer(Modifier.height(16.dp))

    when (viewModel.selectedSearchTab) {
        SearchRequestTab.Profile -> EmptyStateText("Profile is available on the Home tab.")
        SearchRequestTab.FindMedicines -> FindMedicinesContent(viewModel = viewModel)
        SearchRequestTab.DirectShopRequest -> FindShopsContent(
            viewModel = viewModel,
            onArrangementRequest = onArrangementRequest,
        )
        SearchRequestTab.RequestLogs -> EmptyStateText("Request logs are available in the signed-in villager dashboard.")
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchRequestTabRow(
    selectedTab: SearchRequestTab,
    onSelectedTab: (SearchRequestTab) -> Unit,
) {
    val tabs = listOf(
        SearchRequestTab.Profile to "Profile",
        SearchRequestTab.FindMedicines to "Find Medicines",
        SearchRequestTab.DirectShopRequest to "Direct Shop Request",
        SearchRequestTab.RequestLogs to "Request Logs",
    )
    ScrollableTabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab },
        edgePadding = 8.dp,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        tabs.forEach { (tab, label) ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onSelectedTab(tab) },
                text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FindMedicinesContent(viewModel: InventoryViewModel) {
    OutlinedTextField(
        value = viewModel.searchQuery,
        onValueChange = viewModel::onQueryChanged,
        label = { Text("Search medicine name...") },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = RoundedCornerShape(DashboardRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(16.dp))
    VillageSelector(
        villages = viewModel.villageLocations,
        selectedVillageId = viewModel.selectedVillageId,
        onVillageChanged = viewModel::onVillageChanged,
    )
    Spacer(Modifier.height(12.dp))
    RadiusSelector(viewModel.radiusKm, viewModel::onRadiusChanged)

    Spacer(Modifier.height(16.dp))
    ResultList(
        title = "Search Results",
        emptyText = "No stock found. Try a wider radius.",
        results = viewModel.searchResults,
    )
}

@Composable
private fun EmergencyScreen(
    results: List<MedicineSearchResult>,
) {
    ResultList(
        title = "Emergency medicines",
        emptyText = "No emergency stock found inside this radius.",
        results = results,
    )
}

@Composable
private fun FindShopsContent(
    viewModel: InventoryViewModel,
    onArrangementRequest: (ShopSearchResult) -> Unit,
) {
    OutlinedTextField(
        value = viewModel.shopSearchQuery,
        onValueChange = viewModel::onShopQueryChanged,
        label = { Text("Search area or pincode...") },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = RoundedCornerShape(DashboardRadius),
        colors = dashboardTextFieldColors(),
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(16.dp))
    VillageSelector(
        villages = viewModel.villageLocations,
        selectedVillageId = viewModel.selectedVillageId,
        onVillageChanged = viewModel::onVillageChanged,
    )
    Spacer(Modifier.height(12.dp))
    RadiusSelector(viewModel.radiusKm, viewModel::onRadiusChanged)

    Spacer(Modifier.height(16.dp))
    Text(
        text = "Nearby Shops",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(Modifier.height(8.dp))
    if (viewModel.shopResults.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No shops found. Try a wider radius.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.shopResults, key = { it.shop.id }) { result ->
                ShopArrangementCard(
                    result = result,
                    onArrangementRequest = { onArrangementRequest(result) },
                )
            }
        }
    }
}

@Composable
private fun dashboardTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
)

@Composable
private fun ShopArrangementCard(
    result: ShopSearchResult,
    onArrangementRequest: () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.shop.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = result.shop.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                AssistChip(
                    onClick = {},
                    colors = dashboardAssistChipColors(),
                    label = { Text("${String.format(Locale.US, "%.1f", result.distanceKm)} km") },
                )
            }
            Text(
                text = result.shop.operatingHours,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onArrangementRequest,
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("Request Medicine Arrangement")
            }
        }
    }
}

@Composable
private fun StockStatusBadge(status: String) {
    val badgeColor = when (status) {
        "Available" -> AvailabilityGreen
        "Low stock", "Low Stock" -> EmergencyRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = badgeColor.copy(alpha = 0.14f),
        contentColor = badgeColor,
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun LifeSavingBadge() {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = EmergencyRed.copy(alpha = 0.14f),
        contentColor = EmergencyRed,
    ) {
        Text(
            text = "Life Saving",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun ViewDetailsButton(
    expanded: Boolean,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(if (expanded) "Hide Details" else "View Details")
        Spacer(Modifier.size(4.dp))
        Icon(
            imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
            contentDescription = null,
        )
    }
}

@Composable
private fun ExpiryCountdownText(expiryDateMillis: Long) {
    var nowMillis by remember(expiryDateMillis) { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(expiryDateMillis) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(TimeUnit.MINUTES.toMillis(1))
        }
    }

    val daysLeft = TimeUnit.MILLISECONDS.toDays(expiryDateMillis - nowMillis)
    val text = when {
        daysLeft < 0 -> "Expired"
        daysLeft == 0L -> "Expires today"
        daysLeft > 365L -> {
            val yearsLeft = (daysLeft / 365L).coerceAtLeast(1L)
            "$yearsLeft ${if (yearsLeft == 1L) "year" else "years"} left"
        }
        else -> "$daysLeft ${if (daysLeft == 1L) "day" else "days"} to expire"
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = if (daysLeft in 0..30) EmergencyRed else MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun CallLocateActions(result: MedicineSearchResult) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(DashboardRadius),
            color = MaterialTheme.colorScheme.surface,
        ) {
            IconButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${result.shop.contactNo}")))
                },
            ) {
                Icon(
                    Icons.Rounded.Phone,
                    contentDescription = "Call",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.size(8.dp))
        Surface(
            shape = RoundedCornerShape(DashboardRadius),
            color = MaterialTheme.colorScheme.surface,
        ) {
            IconButton(
                onClick = {
                    val destination = Uri.encode("${result.shop.latitude},${result.shop.longitude}")
                    val mapsUri = Uri.parse(
                        "https://www.google.com/maps/dir/?api=1&destination=$destination&travelmode=driving",
                    )
                    context.startActivity(Intent(Intent.ACTION_VIEW, mapsUri))
                },
            ) {
                Icon(
                    Icons.Rounded.LocationOn,
                    contentDescription = "Locate",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Quantity",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = { onQuantityChanged((quantity - 1).coerceAtLeast(1)) },
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text("-")
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Button(
                onClick = { onQuantityChanged((quantity + 1).coerceAtMost(99)) },
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text("+")
            }
        }
    }
}

@Composable
private fun CameraPermissionRationale() {
    Text(
        text = "Camera access is needed to capture a prescription photo.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
    )
}

private fun createPrescriptionImageUri(context: Context): Uri {
    val imageDirectory = File(context.cacheDir, "prescription_images").apply {
        mkdirs()
    }
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFile = File.createTempFile("prescription_$timestamp", ".jpg", imageDirectory)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile,
    )
}

@Composable
private fun RadiusSelector(selectedRadius: Int, onRadiusChanged: (Int) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(5, 10, 15, 20).forEach { radius ->
            FilterChip(
                selected = selectedRadius == radius,
                onClick = { onRadiusChanged(radius) },
                shape = RoundedCornerShape(DashboardRadius),
                colors = dashboardFilterChipColors(),
                label = { Text("${radius} km", fontSize = 16.sp) },
            )
        }
    }
}

@Composable
private fun VillageSelector(
    villages: List<VillageLocation>,
    selectedVillageId: String,
    onVillageChanged: (String) -> Unit,
) {
    Text(
        "Search near",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(Modifier.height(6.dp))
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        villages.forEach { village ->
            FilterChip(
                selected = selectedVillageId == village.id,
                onClick = { onVillageChanged(village.id) },
                shape = RoundedCornerShape(DashboardRadius),
                colors = dashboardFilterChipColors(),
                label = { Text(village.name, fontSize = 16.sp) },
            )
        }
    }
}

@Composable
private fun dashboardFilterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
    containerColor = MaterialTheme.colorScheme.surface,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

private fun googleMapsLocationLabel(latitude: Double, longitude: Double): String =
    "Google Maps location (${String.format(Locale.US, "%.6f", latitude)}, ${String.format(Locale.US, "%.6f", longitude)})"

private fun formatDateMillis(dateMillis: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(dateMillis))

private fun openPhoneDialer(context: Context, phone: String) {
    val cleanPhone = phone.trim()
    if (cleanPhone.isBlank()) return
    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone")))
}

private fun locationSavedMessage(
    liveLocation: android.location.Location,
    liveText: String,
    approximateText: String,
): String = if (isApproximateAppLocation(liveLocation)) approximateText else liveText

private fun openMedicineInGoogleMaps(context: Context, medicine: MedicineItem) {
    val uri = when {
        medicine.latitude != 0.0 || medicine.longitude != 0.0 -> {
            Uri.parse(
                "geo:${medicine.latitude},${medicine.longitude}?q=${medicine.latitude},${medicine.longitude}(${
                    Uri.encode(medicine.shopName.ifBlank { medicine.name })
                })",
            )
        }
        medicine.shopAddress.isNotBlank() || medicine.fullAddress.isNotBlank() -> {
            Uri.parse("geo:0,0?q=${Uri.encode(medicine.shopAddress.ifBlank { medicine.fullAddress })}")
        }
        else -> return
    }
    val mapsIntent = Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps")
    runCatching {
        context.startActivity(mapsIntent)
    }.onFailure {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}

private fun openRequestLocationInMaps(context: Context, request: ArrangementRequest) {
    val latitude = request.villagerLatitude
    val longitude = request.villagerLongitude
    val uri = when {
        latitude != null && longitude != null && (latitude != 0.0 || longitude != 0.0) -> {
            Uri.parse(
                "geo:$latitude,$longitude?q=$latitude,$longitude(${
                    Uri.encode(request.villagerName.ifBlank { "Villager" })
                })",
            )
        }
        request.villagerAddress.isNotBlank() -> {
            Uri.parse("geo:0,0?q=${Uri.encode(request.villagerAddress)}")
        }
        else -> return
    }
    val mapsIntent = Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps")
    runCatching {
        context.startActivity(mapsIntent)
    }.onFailure {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}

private fun openPharmacyInGoogleMaps(context: Context, pharmacy: PharmacistProfile) {
    val latitude = pharmacy.latitude
    val longitude = pharmacy.longitude
    val uri = when {
        latitude != null && longitude != null && (latitude != 0.0 || longitude != 0.0) -> {
            Uri.parse(
                "geo:$latitude,$longitude?q=$latitude,$longitude(${
                    Uri.encode(pharmacy.shopName.ifBlank { "Pharmacy" })
                })",
            )
        }
        pharmacy.address.isNotBlank() -> {
            Uri.parse("geo:0,0?q=${Uri.encode(pharmacy.address)}")
        }
        else -> return
    }
    val mapsIntent = Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps")
    runCatching {
        context.startActivity(mapsIntent)
    }.onFailure {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}

private fun ArrangementRequest.hasVillagerLocation(): Boolean =
    (villagerLatitude != null && villagerLongitude != null && (villagerLatitude != 0.0 || villagerLongitude != 0.0)) ||
        villagerAddress.isNotBlank()

private fun PharmacistProfile.hasLocation(): Boolean =
    (latitude != null && longitude != null && (latitude != 0.0 || longitude != 0.0)) || address.isNotBlank()

private fun ShopResult.distanceFrom(location: VillagerLocationUiState): Double? {
    val startLatitude = location.latitude ?: return null
    val startLongitude = location.longitude ?: return null
    val endLatitude = latitude ?: return null
    val endLongitude = longitude ?: return null
    if (endLatitude == 0.0 && endLongitude == 0.0) return null
    return calculateDistanceKm(startLatitude, startLongitude, endLatitude, endLongitude)
}

private fun calculateDistanceKm(
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

private fun MedicineItem.hasShopLocation(): Boolean =
    latitude != 0.0 || longitude != 0.0 || shopAddress.isNotBlank() || fullAddress.isNotBlank()

@Composable
private fun DarkModeToggle(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Dark mode", style = MaterialTheme.typography.bodyMedium)
        Switch(checked = darkTheme, onCheckedChange = onDarkThemeChanged)
    }
}

@Composable
private fun ResultList(
    title: String,
    emptyText: String,
    results: List<MedicineSearchResult>,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(Modifier.height(8.dp))

    if (results.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(emptyText, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(results, key = { it.stock.id }) { result ->
                MedicineCard(result = result)
            }
        }
    }
}

@Composable
private fun MedicineCard(
    result: MedicineSearchResult,
) {
    ElevatedCard(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.medicine.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${result.shop.name}, ${result.shop.village}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                StockStatusBadge(result.stockStatus)
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AssistChip(
                    onClick = {},
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    label = { Text("${String.format(Locale.US, "%.1f", result.distanceKm)} km") },
                )
                if (result.medicine.requiresPrescription) {
                    AssistChip(
                        onClick = {},
                        colors = dashboardAssistChipColors(),
                        label = { Text("Prescription") },
                    )
                }
                if (result.medicine.isRare) {
                    AssistChip(
                        onClick = {},
                        colors = dashboardAssistChipColors(),
                        label = { Text("Rare") },
                    )
                }
                if (result.expiresSoon) {
                    AssistChip(
                        onClick = {},
                        colors = dashboardAssistChipColors(),
                        label = { Text("Expiry Watch") },
                    )
                }
            }

            CallLocateActions(result)
        }
    }
}

@Composable
private fun dashboardAssistChipColors() = AssistChipDefaults.assistChipColors(
    containerColor = MaterialTheme.colorScheme.surface,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Composable
private fun RequestStatusCard(message: String, onDismiss: () -> Unit) {
    Card(
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    }
}

@Composable
private fun DashboardSettingsDialog(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    roleActionText: String,
    onRoleAction: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Dark mode", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Reduce brightness for low-light use.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(checked = darkTheme, onCheckedChange = onDarkThemeChanged)
                }
                OutlinedButton(
                    onClick = onRoleAction,
                    shape = RoundedCornerShape(DashboardRadius),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(roleActionText)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
    )
}

@Composable
private fun PrescriptionUploadArea(
    onPrescriptionChanged: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var showCameraPermissionMessage by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            onPrescriptionChanged(uri)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            imageUri = pendingCameraUri
            onPrescriptionChanged(pendingCameraUri)
        } else {
            pendingCameraUri = null
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            val uri = createPrescriptionImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            showCameraPermissionMessage = true
        }
    }

    fun launchCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val uri = createPrescriptionImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 156.dp)
            .then(
                if (imageUri != null) {
                    Modifier
                } else {
                    Modifier.clickable { showSourceDialog = true }
                },
            ),
        shape = RoundedCornerShape(DashboardRadius),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        val selectedImageUri = imageUri
        if (selectedImageUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected prescription preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(DashboardRadius)),
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                ) {
                    IconButton(
                        onClick = {
                            imageUri = null
                            pendingCameraUri = null
                            onPrescriptionChanged(null)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Remove prescription",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.UploadFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp),
                )
                Text(
                    text = "Upload Doctor's Prescription (Optional)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Tap to take a photo or choose from gallery",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (showCameraPermissionMessage) {
        Spacer(Modifier.height(8.dp))
        CameraPermissionRationale()
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Upload prescription") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ListItem(
                        modifier = Modifier.clickable {
                            showSourceDialog = false
                            launchCamera()
                        },
                        headlineContent = { Text("Take Photo") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        },
                    )
                    ListItem(
                        modifier = Modifier.clickable {
                            showSourceDialog = false
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                        headlineContent = { Text("Choose from Gallery") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.PhotoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        },
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSourceDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ArrangementRequestDialog(
    result: ShopSearchResult,
    onDismiss: () -> Unit,
    onSubmit: (villagerName: String, villagerPhone: String, medicineName: String, quantity: Int, prescriptionUri: String?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var villagerName by remember(result.shop.id) { mutableStateOf("") }
    var villagerPhone by remember(result.shop.id) { mutableStateOf("") }
    var medicineName by remember(result.shop.id) { mutableStateOf("") }
    var quantity by remember(result.shop.id) { mutableStateOf(1) }
    var prescriptionUri by remember(result.shop.id) { mutableStateOf<Uri?>(null) }
    val canSubmit = villagerName.isNotBlank() &&
        villagerPhone.length == 10 &&
        medicineName.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Medicine Arrangement Request",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = result.shop.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = result.shop.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Surface(
                shape = RoundedCornerShape(DashboardRadius),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Patient contact",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    OutlinedTextField(
                        value = villagerName,
                        onValueChange = { villagerName = it.take(60) },
                        label = { Text("Patient / Your Name") },
                        singleLine = true,
                        colors = dashboardTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = villagerPhone,
                        onValueChange = { villagerPhone = it.filter(Char::isDigit).take(10) },
                        label = { Text("Contact Number") },
                        singleLine = true,
                        colors = dashboardTextFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it.take(80) },
                label = { Text("Medicine Name") },
                singleLine = true,
                colors = dashboardTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            QuantityStepper(
                quantity = quantity,
                onQuantityChanged = { quantity = it },
            )
            PrescriptionUploadArea(
                onPrescriptionChanged = { prescriptionUri = it },
            )
            Button(
                onClick = { onSubmit(villagerName, villagerPhone, medicineName, quantity, prescriptionUri?.toString()) },
                enabled = canSubmit,
                shape = RoundedCornerShape(DashboardRadius),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text("Submit Request")
            }
        }
    }
}

@Composable
private fun PharmacistStockScreen(viewModel: InventoryViewModel) {
    Text("Stock Management", style = MaterialTheme.typography.titleMedium)
    Text("Ravugodlu Medicals stock", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(viewModel.pharmacistStock, key = { it.stock.id }) { result ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(result.medicine.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Expires ${result.stock.expiryDate}", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(
                        onClick = { viewModel.adjustStock(result.stock.id, -1) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(48.dp),
                    ) {
                        Text("-", fontSize = 22.sp)
                    }
                    Text("${result.stock.quantity}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { viewModel.adjustStock(result.stock.id, 1) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(48.dp),
                    ) {
                        Text("+", fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardNavigationBar(
    selected: DashboardSection,
    showStockTab: Boolean,
    onSelected: (DashboardSection) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        NavigationBarItem(
            selected = selected == DashboardSection.Home,
            onClick = { onSelected(DashboardSection.Home) },
            icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
            label = { Text("Home") },
            colors = dashboardNavigationItemColors(),
        )
        NavigationBarItem(
            selected = selected == DashboardSection.Search,
            onClick = { onSelected(DashboardSection.Search) },
            icon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            label = { Text("Search") },
            colors = dashboardNavigationItemColors(),
        )
        if (showStockTab) {
            NavigationBarItem(
                selected = selected == DashboardSection.Stock,
                onClick = { onSelected(DashboardSection.Stock) },
                icon = { Icon(Icons.Rounded.Inventory2, contentDescription = null) },
                label = { Text("Stock") },
                colors = dashboardNavigationItemColors(),
            )
        }
    }
}

@Composable
private fun dashboardNavigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

