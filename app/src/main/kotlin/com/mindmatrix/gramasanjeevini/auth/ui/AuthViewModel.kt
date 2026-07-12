package com.mindmatrix.gramasanjeevini.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.gramasanjeevini.auth.data.AuthRepository
import com.mindmatrix.gramasanjeevini.auth.domain.AuthResult
import com.mindmatrix.gramasanjeevini.auth.domain.PharmacistSignupForm
import com.mindmatrix.gramasanjeevini.auth.domain.UserRole
import com.mindmatrix.gramasanjeevini.auth.domain.VillagerSignupForm
import com.mindmatrix.gramasanjeevini.core.AsyncUiState
import com.mindmatrix.gramasanjeevini.core.Validation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ACCOUNT_CREATED_VERIFICATION_MESSAGE =
    "Account created successfully! Please check your email inbox to verify your account."

private const val UNVERIFIED_EMAIL_MESSAGE =
    "Your email is not verified. Please check your inbox and verify your email to continue."

private const val VERIFICATION_EMAIL_RESENT_MESSAGE =
    "Verification email resent. Please check your inbox."

private const val PASSWORD_RESET_SENT_MESSAGE =
    "Password reset link sent to your email"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthScreenState())
    val uiState: StateFlow<AuthScreenState> = _uiState.asStateFlow()

    fun signupVillager(form: VillagerSignupForm) {
        val validationError = Validation.villagerSignup(form)
        if (validationError != null) {
            _uiState.update {
                it.copy(
                    status = AsyncUiState(message = validationError),
                    authenticatedRole = null,
                    canResendVerification = false,
                    isSuccessMessage = false,
                )
            }
            return
        }

        runSignupAction {
            authRepository.signupVillager(form)
        }
    }

    fun signUpVillager(
        fullName: String,
        phone: String,
        email: String,
        password: String,
        confirmPassword: String,
        address: String,
        latitude: Double?,
        longitude: Double?,
    ) {
        signupVillager(
            VillagerSignupForm(
                fullName = fullName,
                phone = phone,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                address = address,
                latitude = latitude,
                longitude = longitude,
            ),
        )
    }

    fun signupPharmacist(form: PharmacistSignupForm) {
        val validationError = Validation.pharmacistSignup(form)
        if (validationError != null) {
            _uiState.update {
                it.copy(
                    status = AsyncUiState(message = validationError),
                    authenticatedRole = null,
                    canResendVerification = false,
                    isSuccessMessage = false,
                )
            }
            return
        }

        runSignupAction {
            authRepository.signupPharmacist(form)
        }
    }

    fun signUpPharmacist(
        email: String,
        password: String,
        confirmPassword: String,
        shopName: String,
        ownerName: String,
        drugLicenseNumber: String,
        pharmacistRegNumber: String,
        phone: String,
        address: String,
        latitude: Double?,
        longitude: Double?,
    ) {
        signupPharmacist(
            PharmacistSignupForm(
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
            ),
        )
    }

    fun loginPharmacist(email: String, password: String) {
        login(email, password, UserRole.Pharmacist)
    }

    fun loginVillager(email: String, password: String) {
        login(email, password, UserRole.Villager)
    }

    fun loginAdmin(email: String, password: String) {
        login(email, password, UserRole.Admin)
    }

    fun resendVerificationEmail(email: String, password: String, role: UserRole) {
        val validationError = Validation.login(email, password)
        if (validationError != null) {
            _uiState.update {
                it.copy(
                    status = AsyncUiState(message = validationError),
                    isSuccessMessage = false,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    status = AsyncUiState(isLoading = true),
                    isSuccessMessage = false,
                )
            }
            when (val result = authRepository.resendVerificationEmail(email, password, role)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(
                        status = AsyncUiState(message = VERIFICATION_EMAIL_RESENT_MESSAGE),
                        authenticatedRole = null,
                        canResendVerification = false,
                        isSuccessMessage = true,
                        verificationEmailResentCount = it.verificationEmailResentCount + 1,
                    )
                }
                is AuthResult.Failure -> _uiState.update {
                    it.copy(
                        status = AsyncUiState(message = result.message),
                        authenticatedRole = null,
                        canResendVerification = result.message == UNVERIFIED_EMAIL_MESSAGE,
                        isSuccessMessage = false,
                    )
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        val validationError = Validation.forgotPassword(email)
        if (validationError != null) {
            _uiState.update {
                it.copy(
                    status = AsyncUiState(message = validationError),
                    isSuccessMessage = false,
                    passwordResetMessage = validationError,
                    passwordResetSucceeded = false,
                    passwordResetEventCount = it.passwordResetEventCount + 1,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    status = AsyncUiState(isLoading = true),
                    isSuccessMessage = false,
                    passwordResetMessage = null,
                    passwordResetSucceeded = false,
                )
            }
            when (val result = authRepository.sendPasswordReset(email)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(
                        status = AsyncUiState(message = PASSWORD_RESET_SENT_MESSAGE),
                        isSuccessMessage = true,
                        passwordResetMessage = PASSWORD_RESET_SENT_MESSAGE,
                        passwordResetSucceeded = true,
                        passwordResetEventCount = it.passwordResetEventCount + 1,
                    )
                }
                is AuthResult.Failure -> _uiState.update {
                    val message = "Error: ${result.message}"
                    it.copy(
                        status = AsyncUiState(message = message),
                        isSuccessMessage = false,
                        passwordResetMessage = message,
                        passwordResetSucceeded = false,
                        passwordResetEventCount = it.passwordResetEventCount + 1,
                    )
                }
            }
        }
    }

    fun sendPasswordReset(email: String) {
        sendPasswordResetEmail(email)
    }

    fun clearMessage() {
        _uiState.update { it.copy(status = it.status.copy(message = null), isSuccessMessage = false) }
    }

    fun consumeNavigation() {
        _uiState.update { it.copy(authenticatedRole = null) }
    }

    fun logout() {
        _uiState.value = AuthScreenState()
        viewModelScope.coroutineContext.cancelChildren()
        authRepository.logout()
    }

    private fun login(email: String, password: String, role: UserRole) {
        val validationError = Validation.login(email, password)
        if (validationError != null) {
            _uiState.update {
                it.copy(
                    status = AsyncUiState(message = validationError),
                    authenticatedRole = null,
                    canResendVerification = false,
                    isSuccessMessage = false,
                )
            }
            return
        }

        runAuthAction {
            authRepository.login(email, password, role)
        }
    }

    private fun runAuthAction(block: suspend () -> AuthResult) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    status = AsyncUiState(isLoading = true),
                    canResendVerification = false,
                    isSuccessMessage = false,
                )
            }
            when (val result = block()) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(
                        status = AsyncUiState(),
                        authenticatedRole = result.profile.role,
                        canResendVerification = false,
                        isSuccessMessage = false,
                    )
                }
                is AuthResult.Failure -> _uiState.update {
                    it.copy(
                        status = AsyncUiState(message = result.message),
                        authenticatedRole = null,
                        canResendVerification = result.message == UNVERIFIED_EMAIL_MESSAGE,
                        isSuccessMessage = false,
                    )
                }
            }
        }
    }

    private fun runSignupAction(block: suspend () -> AuthResult) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    status = AsyncUiState(isLoading = true),
                    authenticatedRole = null,
                    canResendVerification = false,
                    isSuccessMessage = false,
                )
            }
            when (val result = block()) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(
                        status = AsyncUiState(message = ACCOUNT_CREATED_VERIFICATION_MESSAGE),
                        authenticatedRole = null,
                        canResendVerification = false,
                        isSuccessMessage = true,
                    )
                }
                is AuthResult.Failure -> _uiState.update {
                    it.copy(
                        status = AsyncUiState(message = result.message),
                        authenticatedRole = null,
                        canResendVerification = false,
                        isSuccessMessage = false,
                    )
                }
            }
        }
    }
}

data class AuthScreenState(
    val status: AsyncUiState = AsyncUiState(),
    val authenticatedRole: UserRole? = null,
    val canResendVerification: Boolean = false,
    val isSuccessMessage: Boolean = false,
    val verificationEmailResentCount: Int = 0,
    val passwordResetMessage: String? = null,
    val passwordResetSucceeded: Boolean = false,
    val passwordResetEventCount: Int = 0,
)
