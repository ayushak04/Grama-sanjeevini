package com.mindmatrix.gramasanjeevini.dashboard.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mindmatrix.gramasanjeevini.data.MedicineItem
import com.mindmatrix.gramasanjeevini.ui.AvailabilityGreen
import com.mindmatrix.gramasanjeevini.ui.EmergencyRed
import java.util.Locale

private val SearchScreenRadius = 12.dp

private enum class VillagerBottomTab {
    Search,
    Emergency,
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun VillagerSearchScreen(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onRolesProfileClick: () -> Unit,
    onRequestArrangement: (MedicineItem) -> Unit,
    viewModel: VillagerViewModel = hiltViewModel(),
) {
    val query by viewModel.medicineQuery.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val selectedDistanceKm by viewModel.selectedDistanceKm.collectAsState()
    val state by viewModel.medicineState.collectAsState()
    var selectedTab by remember { mutableStateOf(VillagerBottomTab.Search) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Medicine Search",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "Find nearby stock and request pharmacy support",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                actions = {
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
                        IconButton(onClick = onRolesProfileClick) {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = "Roles and profile",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = selectedTab == VillagerBottomTab.Search,
                    onClick = { selectedTab = VillagerBottomTab.Search },
                    icon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    label = { Text("Search") },
                    colors = villagerNavigationColors(),
                )
                NavigationBarItem(
                    selected = selectedTab == VillagerBottomTab.Emergency,
                    onClick = { selectedTab = VillagerBottomTab.Emergency },
                    icon = { Icon(Icons.Rounded.LocalHospital, contentDescription = null) },
                    label = { Text("Emergency") },
                    colors = villagerNavigationColors(),
                )
            }
        },
    ) { padding ->
        when (val currentState = state) {
            MedicineUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is MedicineUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = currentState.message,
                        style = MaterialTheme.typography.titleMedium,
                        color = EmergencyRed,
                    )
                }
            }

            is MedicineUiState.Success -> {
                if (currentState.medicines.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No medicines found in database",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                } else {
                    val visibleMedicines = when (selectedTab) {
                        VillagerBottomTab.Search -> currentState.medicines.filter { medicine ->
                            val matchesQuery = query.isBlank() ||
                                medicine.medicineName.contains(query, ignoreCase = true)
                            val matchesLocation = selectedLocation.isBlank() ||
                                medicine.shopArea.equals(selectedLocation, ignoreCase = true)
                            val matchesDistance = medicine.distanceKm?.let { it <= selectedDistanceKm } ?: true

                            matchesQuery && matchesLocation && matchesDistance
                        }
                        VillagerBottomTab.Emergency -> currentState.medicines
                            .filter { it.isLifeSaving || it.requiresPrescription }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        item {
                            SearchAndFilters(
                                query = query,
                                onQueryChanged = viewModel::onMedicineQueryChanged,
                                locations = viewModel.locationFilters,
                                selectedLocation = selectedLocation,
                                onLocationSelected = viewModel::onLocationSelected,
                                distances = viewModel.distanceFilters,
                                selectedDistanceKm = selectedDistanceKm,
                                onDistanceSelected = viewModel::onDistanceSelected,
                            )
                        }

                        if (visibleMedicines.isEmpty()) {
                            item {
                                EmptyMedicineResults(message = "No medicines match this search yet.")
                            }
                        } else {
                            items(visibleMedicines, key = { it.id }) { medicine ->
                                MedicineSearchResultCard(
                                    medicine = medicine,
                                    onRequestArrangement = { onRequestArrangement(medicine) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchAndFilters(
    query: String,
    onQueryChanged: (String) -> Unit,
    locations: List<String>,
    selectedLocation: String,
    onLocationSelected: (String) -> Unit,
    distances: List<Int>,
    selectedDistanceKm: Int,
    onDistanceSelected: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            label = { Text("Medicine name") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(SearchScreenRadius),
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = "Search near",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            locations.forEach { location ->
                FilterChip(
                    selected = selectedLocation == location,
                    onClick = { onLocationSelected(location) },
                    label = { Text(location) },
                    colors = villagerFilterChipColors(),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            distances.forEach { distance ->
                FilterChip(
                    selected = selectedDistanceKm == distance,
                    onClick = { onDistanceSelected(distance) },
                    label = { Text("$distance km") },
                    colors = villagerFilterChipColors(),
                )
            }
        }
    }
}

@Composable
private fun MedicineSearchResultCard(
    medicine: MedicineItem,
    onRequestArrangement: () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(SearchScreenRadius),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = medicine.medicineName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (medicine.isLifeSaving) {
                    StatusBadge(
                        text = "Life Saving",
                        containerColor = EmergencyRed.copy(alpha = 0.14f),
                        contentColor = EmergencyRed,
                    )
                }
            }

            Text(
                text = listOf(medicine.shopName, medicine.shopArea)
                    .filter { it.isNotBlank() }
                    .joinToString(", "),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = medicine.shopAddress.ifBlank {
                    medicine.fullAddress.ifBlank { "Address details unavailable" }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SearchAssistChip(
                    medicine.distanceKm?.let { "${String.format(Locale.US, "%.1f", it)} km" }
                        ?: "Distance unavailable",
                )
                SearchAssistChip(medicine.stockStatus)
                if (medicine.requiresPrescription) {
                    SearchAssistChip("Prescription")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Qty ${medicine.quantity}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                MedicineCallButton(medicine)
                MedicineLocateButton(medicine)
            }

            Button(
                onClick = onRequestArrangement,
                shape = RoundedCornerShape(SearchScreenRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("Request arrangement")
            }
        }
    }
}

@Composable
private fun MedicineCallButton(medicine: MedicineItem) {
    val context = LocalContext.current
    FilledTonalButton(
        onClick = {
            if (medicine.shopPhone.isNotBlank()) {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${medicine.shopPhone}")))
            }
        },
        enabled = medicine.shopPhone.isNotBlank(),
        shape = RoundedCornerShape(SearchScreenRadius),
    ) {
        Icon(Icons.Rounded.Phone, contentDescription = null)
        Spacer(Modifier.padding(horizontal = 3.dp))
        Text("Call")
    }
}

@Composable
private fun MedicineLocateButton(medicine: MedicineItem) {
    val context = LocalContext.current
    FilledTonalButton(
        onClick = {
            val uri = if (medicine.latitude != 0.0 || medicine.longitude != 0.0) {
                Uri.parse("geo:${medicine.latitude},${medicine.longitude}?q=${medicine.latitude},${medicine.longitude}(${Uri.encode(medicine.shopName)})")
            } else {
                Uri.parse("geo:0,0?q=${Uri.encode(medicine.shopAddress.ifBlank { medicine.fullAddress.ifBlank { medicine.shopName } })}")
            }
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        },
        shape = RoundedCornerShape(SearchScreenRadius),
    ) {
        Icon(Icons.Rounded.LocationOn, contentDescription = null)
        Spacer(Modifier.padding(horizontal = 3.dp))
        Text("Locate")
    }
}

@Composable
private fun SearchAssistChip(text: String) {
    AssistChip(
        onClick = {},
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Composable
private fun StatusBadge(
    text: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = containerColor,
        contentColor = contentColor,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun EmptyMedicineResults(message: String) {
    Surface(
        shape = RoundedCornerShape(SearchScreenRadius),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(18.dp),
        )
    }
}

@Composable
private fun villagerFilterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
    containerColor = MaterialTheme.colorScheme.surface,
    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Composable
private fun villagerNavigationColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
)
