package com.mindmatrix.gramasanjeevini.auth.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mindmatrix.gramasanjeevini.auth.domain.UserRole
import com.mindmatrix.gramasanjeevini.components.AuthBackground
import com.mindmatrix.gramasanjeevini.components.AuthScreenFrame
import com.mindmatrix.gramasanjeevini.components.AuthTextField
import com.mindmatrix.gramasanjeevini.components.LargeActionButton
import com.mindmatrix.gramasanjeevini.components.LargeOutlineButton
import com.mindmatrix.gramasanjeevini.components.MessageCard
import com.mindmatrix.gramasanjeevini.components.PasswordTextField
import com.mindmatrix.gramasanjeevini.core.NativeLocationPermissions
import com.mindmatrix.gramasanjeevini.core.fetchNativeLiveLocation
import com.mindmatrix.gramasanjeevini.core.hasNativeLocationPermission
import com.mindmatrix.gramasanjeevini.core.isApproximateAppLocation
import com.mindmatrix.gramasanjeevini.ui.EmergencyRed
import com.mindmatrix.gramasanjeevini.ui.PrimaryBlue

@Composable
fun SplashScreen(
    onRoleSelection: () -> Unit,
    onAuthenticatedRole: (UserRole) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val startRole by viewModel.startRole.collectAsState()
    val shouldShowRoleSelection by viewModel.shouldShowRoleSelection.collectAsState()
    val transition = rememberInfiniteTransition(label = "splash")
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "logoAlpha",
    )

    LaunchedEffect(Unit) {
        viewModel.resolveSession()
    }
    LaunchedEffect(startRole) {
        startRole?.let(onAuthenticatedRole)
    }
    LaunchedEffect(shouldShowRoleSelection) {
        if (shouldShowRoleSelection) onRoleSelection()
    }

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .alpha(alpha)
                    .background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("+", color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(20.dp))
            Text("Grama-Sanjeevini", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text("Rural Healthcare Network", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun RoleSelectionScreen(
    onVillagerLogin: () -> Unit,
    onVillagerGuest: () -> Unit,
    onPharmacistLogin: () -> Unit,
    onAdminLogin: () -> Unit,
) {
    val subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant
    var selectedRole by remember { mutableStateOf<RoleSelection?>(null) }

    AuthBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.weight(0.65f))

                Column(
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.HealthAndSafety,
                            contentDescription = "Grama-Sanjeevini logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                        )
                    }

                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = "Grama-Sanjeevini",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Nearby medicine help for every village.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = subtitleColor,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(Modifier.height(44.dp))

                Column(
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Choose your role",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Find medicines, connect with shops, and manage rural health access.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = subtitleColor,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(22.dp))

                    RoleSelectionButton(
                        text = "Login / Signup as Villager",
                        selected = selectedRole == RoleSelection.Villager,
                        onClick = {
                            selectedRole = RoleSelection.Villager
                            onVillagerLogin()
                        },
                    )

                    Spacer(Modifier.height(12.dp))
                    RoleSelectionButton(
                        text = "Continue as Guest",
                        selected = selectedRole == RoleSelection.Guest,
                        onClick = {
                            selectedRole = RoleSelection.Guest
                            onVillagerGuest()
                        },
                    )

                    Spacer(Modifier.height(12.dp))
                    RoleSelectionButton(
                        text = "Login / Signup as Pharmacist",
                        selected = selectedRole == RoleSelection.Pharmacist,
                        onClick = {
                            selectedRole = RoleSelection.Pharmacist
                            onPharmacistLogin()
                        },
                    )
                }

                Spacer(Modifier.weight(1f))

                TextButton(
                    onClick = onAdminLogin,
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = "Login as Admin",
                        style = MaterialTheme.typography.labelLarge,
                        color = subtitleColor,
                    )
                }
            }
        }
    }
}

private enum class RoleSelection {
    Villager,
    Guest,
    Pharmacist,
}

@Composable
private fun RoleSelectionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val unselectedContainer = lerp(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.primary,
        if (isSystemInDarkTheme()) 0.18f else 0.10f,
    )

    FilledTonalButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                unselectedContainer
            },
            contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun VillagerLoginScreen(
    onSignup: () -> Unit,
    onForgotPassword: () -> Unit,
    onAuthenticated: () -> Unit,
    onGuestAccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    PasswordResetFeedbackToast(state) {
        showResetPasswordDialog = false
    }

    LaunchedEffect(state.authenticatedRole) {
        if (state.authenticatedRole == UserRole.Villager) {
            viewModel.consumeNavigation()
            onAuthenticated()
        }
    }

    AuthScreenFrame(
        title = "Villager Login",
        subtitle = "Save your contact details and track medicine arrangement requests.",
    ) {
        VerificationResendToast(state)
        AuthStatusMessage(state)
        AuthTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
        PasswordTextField(password, { password = it }, "Password")
        LargeActionButton(
            text = "Login",
            isLoading = state.status.isLoading,
        ) {
            viewModel.loginVillager(email, password)
        }
        ResendVerificationEmailButton(
            state = state,
            email = email,
            password = password,
            role = UserRole.Villager,
            viewModel = viewModel,
        )
        TextButton(
            onClick = { showResetPasswordDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Forgot password?", fontSize = 18.sp)
        }
        LargeOutlineButton("Create villager account", onClick = onSignup)
        TextButton(onClick = onGuestAccess, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Continue as guest", fontSize = 18.sp)
        }
    }

    if (showResetPasswordDialog) {
        PasswordResetDialog(
            initialEmail = email,
            isLoading = state.status.isLoading,
            onDismiss = { showResetPasswordDialog = false },
            onSendLink = viewModel::sendPasswordResetEmail,
        )
    }
}

@Composable
fun VillagerSignupScreen(
    onAuthenticated: () -> Unit,
    onLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(state.authenticatedRole) {
        if (state.authenticatedRole == UserRole.Villager) {
            viewModel.consumeNavigation()
            onAuthenticated()
        }
    }

    AuthScreenFrame(
        title = "Villager Signup",
        subtitle = "Create an account so nearby shops can contact you for medicine requests.",
    ) {
        AuthStatusMessage(state)
        AuthTextField(fullName, { fullName = it }, "Patient / Your Name")
        AuthTextField(phone, { phone = it.filter(Char::isDigit).take(10) }, "Contact Number", keyboardType = KeyboardType.Phone)
        AuthTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
        PasswordTextField(password, { password = it }, "Password")
        PasswordTextField(confirmPassword, { confirmPassword = it }, "Confirm Password")
        SignupLocationFields(
            address = address,
            onAddressChange = { address = it },
            hasCoordinates = latitude != null && longitude != null,
            onLocationFetched = { fetchedLatitude, fetchedLongitude ->
                latitude = fetchedLatitude
                longitude = fetchedLongitude
            },
        )
        LargeActionButton("Create Villager Account", state.status.isLoading) {
            viewModel.signUpVillager(
                fullName = fullName,
                phone = phone,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                address = address,
                latitude = latitude,
                longitude = longitude,
            )
        }
        TextButton(onClick = onLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Already have an account? Login", fontSize = 18.sp)
        }
    }
}

@Composable
fun PharmacistLoginScreen(
    onSignup: () -> Unit,
    onForgotPassword: () -> Unit,
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    PasswordResetFeedbackToast(state) {
        showResetPasswordDialog = false
    }

    LaunchedEffect(state.authenticatedRole) {
        if (state.authenticatedRole == UserRole.Pharmacist) {
            viewModel.consumeNavigation()
            onAuthenticated()
        }
    }

    AuthScreenFrame(
        title = "Pharmacist Login",
        subtitle = "Use your registered email to manage shop stock and expiry alerts.",
    ) {
        VerificationResendToast(state)
        AuthStatusMessage(state)
        AuthTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
        PasswordTextField(password, { password = it }, "Password")
        LargeActionButton(
            text = "Login",
            isLoading = state.status.isLoading,
        ) {
            viewModel.loginPharmacist(email, password)
        }
        ResendVerificationEmailButton(
            state = state,
            email = email,
            password = password,
            role = UserRole.Pharmacist,
            viewModel = viewModel,
        )
        TextButton(
            onClick = { showResetPasswordDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Forgot password?", fontSize = 18.sp)
        }
        LargeOutlineButton("Create pharmacist account", onClick = onSignup)
    }

    if (showResetPasswordDialog) {
        PasswordResetDialog(
            initialEmail = email,
            isLoading = state.status.isLoading,
            onDismiss = { showResetPasswordDialog = false },
            onSendLink = viewModel::sendPasswordResetEmail,
        )
    }
}

@Composable
fun PharmacistSignupScreen(
    onAuthenticated: () -> Unit,
    onLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var drugLicenseNumber by remember { mutableStateOf("") }
    var pharmacistRegNumber by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(state.authenticatedRole) {
        if (state.authenticatedRole == UserRole.Pharmacist) {
            viewModel.consumeNavigation()
            onAuthenticated()
        }
    }

    AuthScreenFrame(
        title = "Pharmacist Signup",
        subtitle = "Create a verified shop account for stock updates.",
    ) {
        AuthStatusMessage(state)
        AuthTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
        PasswordTextField(password, { password = it }, "Password")
        PasswordTextField(confirmPassword, { confirmPassword = it }, "Confirm Password")
        AuthTextField(shopName, { shopName = it }, "Shop Name")
        AuthTextField(ownerName, { ownerName = it }, "Owner Name")
        AuthTextField(
            value = drugLicenseNumber,
            onValueChange = { drugLicenseNumber = it.uppercase().take(120) },
            label = "Drug License Number (Form 20/21)",
            singleLine = false,
            minLines = 2,
            maxLines = 3,
            imeAction = ImeAction.Default,
        )
        AuthTextField(
            pharmacistRegNumber,
            { pharmacistRegNumber = it.uppercase().take(40) },
            "Registered Pharmacist Number (State Council ID)",
        )
        AuthTextField(phone, { phone = it.filter(Char::isDigit).take(10) }, "Phone Number", keyboardType = KeyboardType.Phone)
        SignupLocationFields(
            address = address,
            onAddressChange = { address = it },
            hasCoordinates = latitude != null && longitude != null,
            onLocationFetched = { fetchedLatitude, fetchedLongitude ->
                latitude = fetchedLatitude
                longitude = fetchedLongitude
            },
        )
        LargeActionButton("Create Account", state.status.isLoading) {
            viewModel.signUpPharmacist(
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                shopName = shopName,
                ownerName = ownerName,
                drugLicenseNumber = drugLicenseNumber,
                pharmacistRegNumber = pharmacistRegNumber,
                phone = phone,
                address = address,
                latitude = latitude,
                longitude = longitude,
            )
        }
        TextButton(onClick = onLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Already have an account? Login", fontSize = 18.sp)
        }
    }
}

@Composable
private fun SignupLocationFields(
    address: String,
    onAddressChange: (String) -> Unit,
    hasCoordinates: Boolean,
    onLocationFetched: (Double, Double) -> Unit,
) {
    val context = LocalContext.current
    var statusMessage by remember { mutableStateOf<String?>(null) }
    fun saveLocationWithFallback(permissionDenied: Boolean = false) {
        statusMessage = "Fetching your live location..."
        fetchNativeLiveLocation(
            context = context,
            fallbackAddress = address,
            onLocationFetched = { liveLocation ->
                onLocationFetched(liveLocation.latitude, liveLocation.longitude)
                statusMessage = when {
                    isApproximateAppLocation(liveLocation) && permissionDenied -> "Permission denied. Approximate location saved."
                    isApproximateAppLocation(liveLocation) -> "Approximate location saved."
                    else -> "Live location saved."
                }
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
            saveLocationWithFallback()
        } else {
            saveLocationWithFallback(permissionDenied = true)
        }
    }

    Button(
        onClick = {
            if (hasNativeLocationPermission(context)) {
                saveLocationWithFallback()
            } else {
                locationPermissionLauncher.launch(NativeLocationPermissions)
            }
        },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
    ) {
        Icon(Icons.Rounded.LocationOn, contentDescription = null)
        Spacer(Modifier.size(8.dp))
        Text("Fetch Live Location")
    }
    statusMessage?.let {
        Text(
            text = if (hasCoordinates) "$it Coordinates saved privately." else it,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    AuthTextField(
        value = address,
        onValueChange = { onAddressChange(it.take(180)) },
        label = "Full Address / Landmark",
    )
}

@Composable
private fun PasswordResetFeedbackToast(
    state: AuthScreenState,
    onSuccess: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(state.passwordResetEventCount) {
        if (state.passwordResetEventCount > 0) {
            state.passwordResetMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            if (state.passwordResetSucceeded) {
                onSuccess()
            }
        }
    }
}

@Composable
private fun PasswordResetDialog(
    initialEmail: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSendLink: (String) -> Unit,
) {
    var resetEmail by remember(initialEmail) { mutableStateOf(initialEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Enter your registered email address to receive a password reset link.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = { resetEmail = it.take(100) },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSendLink(resetEmail) },
                enabled = !isLoading,
            ) {
                Text("Send Link")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    val isSuccess = state.status.message?.contains("sent", ignoreCase = true) == true

    AuthScreenFrame(
        title = "Forgot Password",
        subtitle = "Enter your email. Firebase will send a reset link if the account exists.",
    ) {
        MessageCard(state.status.message, isError = !isSuccess)
        AuthTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
        LargeActionButton("Send Reset Email", state.status.isLoading) {
            viewModel.sendPasswordResetEmail(email)
        }
        LargeOutlineButton("Back to Login", onClick = onBackToLogin)
    }
}

@Composable
fun AdminLoginScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state.authenticatedRole) {
        if (state.authenticatedRole == UserRole.Admin) {
            viewModel.consumeNavigation()
            onAuthenticated()
        }
    }

    AuthScreenFrame(
        title = "Admin Login",
        subtitle = "Secure access for authorized administrators only.",
    ) {
        VerificationResendToast(state)
        AuthStatusMessage(state)
        AuthTextField(email, { email = it }, "Admin Email", keyboardType = KeyboardType.Email)
        PasswordTextField(password, { password = it }, "Password")
        LargeActionButton(
            text = "Login as Admin",
            isLoading = state.status.isLoading,
            containerColor = EmergencyRed,
        ) {
            viewModel.loginAdmin(email, password)
        }
        ResendVerificationEmailButton(
            state = state,
            email = email,
            password = password,
            role = UserRole.Admin,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun AuthStatusMessage(state: AuthScreenState) {
    MessageCard(
        message = state.status.message,
        isError = !state.isSuccessMessage,
    )
}

@Composable
private fun VerificationResendToast(state: AuthScreenState) {
    val context = LocalContext.current
    LaunchedEffect(state.verificationEmailResentCount) {
        if (state.verificationEmailResentCount > 0) {
            Toast.makeText(context, "Verification email resent. Please check your inbox.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun ResendVerificationEmailButton(
    state: AuthScreenState,
    email: String,
    password: String,
    role: UserRole,
    viewModel: AuthViewModel,
) {
    if (!state.canResendVerification) return

    TextButton(
        onClick = { viewModel.resendVerificationEmail(email, password, role) },
        enabled = !state.status.isLoading,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Resend Verification Email", fontSize = 18.sp)
    }
}
