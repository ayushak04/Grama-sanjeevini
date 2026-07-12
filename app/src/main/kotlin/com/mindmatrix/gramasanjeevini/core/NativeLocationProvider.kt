package com.mindmatrix.gramasanjeevini.core

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import java.util.Locale

val NativeLocationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

fun hasNativeLocationPermission(context: Context): Boolean =
    NativeLocationPermissions.any { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

@SuppressLint("MissingPermission")
fun fetchNativeLiveLocation(
    context: Context,
    fallbackAddress: String = "",
    onLocationFetched: (Location) -> Unit,
    onLocationUnavailable: () -> Unit = {},
) {
    val appContext = context.applicationContext

    fun finishWithFallback() {
        fetchAddressOrServiceAreaLocation(
            context = appContext,
            fallbackAddress = fallbackAddress,
            onLocationFetched = onLocationFetched,
            onLocationUnavailable = onLocationUnavailable,
        )
    }

    if (!hasNativeLocationPermission(context)) {
        finishWithFallback()
        return
    }

    val cachedLocation = currentBestNativeLocation(context)
    if (cachedLocation != null) {
        onLocationFetched(cachedLocation)
        return
    }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    if (locationManager == null) {
        finishWithFallback()
        return
    }

    val providers = enabledNativeProviders(context, locationManager)
    if (providers.isEmpty()) {
        finishWithFallback()
        return
    }

    val mainHandler = Handler(Looper.getMainLooper())
    val listeners = mutableListOf<LocationListener>()
    var completed = false
    lateinit var timeout: Runnable

    fun finish(location: Location?) {
        if (completed) return
        completed = true
        mainHandler.removeCallbacks(timeout)
        listeners.forEach { listener ->
            runCatching { locationManager.removeUpdates(listener) }
        }
        if (location != null && location.hasUsableCoordinates()) {
            onLocationFetched(location)
        } else {
            finishWithFallback()
        }
    }

    timeout = Runnable { finish(null) }
    var requestedProviderCount = 0
    providers.forEach { provider ->
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (location.hasUsableCoordinates()) {
                    finish(location)
                }
            }
        }
        runCatching {
            locationManager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
            listeners += listener
            requestedProviderCount += 1
        }
    }

    if (requestedProviderCount == 0) {
        finish(null)
    } else {
        mainHandler.postDelayed(timeout, LOCATION_FETCH_TIMEOUT_MS)
    }
}

fun isApproximateAppLocation(location: Location): Boolean =
    location.provider == ADDRESS_FALLBACK_PROVIDER || location.provider == SERVICE_AREA_FALLBACK_PROVIDER

@SuppressLint("MissingPermission")
fun currentBestNativeLocation(context: Context): Location? {
    if (!hasNativeLocationPermission(context)) return null
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

    return enabledNativeProviders(context, locationManager)
        .mapNotNull { provider ->
            runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
        }
        .filter { location -> location.hasUsableCoordinates() }
        .maxByOrNull { location -> location.time }
}

private fun enabledNativeProviders(
    context: Context,
    locationManager: LocationManager,
): List<String> {
    val enabledProviders = runCatching { locationManager.getProviders(true) }.getOrDefault(emptyList())
    val preferredProviders = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER,
    )

    return (preferredProviders + enabledProviders)
        .distinct()
        .filter { provider -> provider in enabledProviders }
        .filter { provider -> isProviderAllowedByPermission(context, provider) }
}

private fun isProviderAllowedByPermission(context: Context, provider: String): Boolean {
    val hasFine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    return when (provider) {
        LocationManager.GPS_PROVIDER -> hasFine
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER,
        -> hasFine || hasCoarse
        else -> hasFine || hasCoarse
    }
}

private fun Location.hasUsableCoordinates(): Boolean =
    latitude in -90.0..90.0 &&
        longitude in -180.0..180.0 &&
        !(latitude == 0.0 && longitude == 0.0)

private fun fetchAddressOrServiceAreaLocation(
    context: Context,
    fallbackAddress: String,
    onLocationFetched: (Location) -> Unit,
    onLocationUnavailable: () -> Unit,
) {
    val cleanedAddress = fallbackAddress.trim()
    if (cleanedAddress.isBlank()) {
        onLocationFetched(defaultServiceAreaLocation())
        return
    }

    Thread {
        val geocodedLocation = geocodeAddress(context, cleanedAddress)
        Handler(Looper.getMainLooper()).post {
            if (geocodedLocation != null) {
                onLocationFetched(geocodedLocation)
            } else {
                onLocationFetched(defaultServiceAreaLocation())
            }
        }
    }.start()
}

private fun geocodeAddress(context: Context, address: String): Location? = runCatching {
    val geocoder = Geocoder(context, Locale.getDefault())
    @Suppress("DEPRECATION")
    geocoder.getFromLocationName(address, 1)
        ?.firstOrNull()
        ?.let { result ->
            Location(ADDRESS_FALLBACK_PROVIDER).apply {
                latitude = result.latitude
                longitude = result.longitude
                accuracy = ADDRESS_FALLBACK_ACCURACY_METERS
                time = System.currentTimeMillis()
            }
        }
        ?.takeIf { location -> location.hasUsableCoordinates() }
}.getOrNull()

private fun defaultServiceAreaLocation(): Location =
    Location(SERVICE_AREA_FALLBACK_PROVIDER).apply {
        latitude = DEFAULT_SERVICE_AREA_LATITUDE
        longitude = DEFAULT_SERVICE_AREA_LONGITUDE
        accuracy = SERVICE_AREA_FALLBACK_ACCURACY_METERS
        time = System.currentTimeMillis()
    }

private const val LOCATION_FETCH_TIMEOUT_MS = 15_000L
private const val ADDRESS_FALLBACK_PROVIDER = "address_fallback"
private const val SERVICE_AREA_FALLBACK_PROVIDER = "service_area_fallback"
private const val ADDRESS_FALLBACK_ACCURACY_METERS = 1_500f
private const val SERVICE_AREA_FALLBACK_ACCURACY_METERS = 15_000f
private const val DEFAULT_SERVICE_AREA_LATITUDE = 12.7200
private const val DEFAULT_SERVICE_AREA_LONGITUDE = 77.4300
