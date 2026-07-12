package com.mindmatrix.gramasanjeevini.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.gramasanjeevini.auth.data.AuthRepository
import com.mindmatrix.gramasanjeevini.auth.domain.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _startRole = MutableStateFlow<UserRole?>(null)
    val startRole: StateFlow<UserRole?> = _startRole.asStateFlow()

    private val _shouldShowRoleSelection = MutableStateFlow(false)
    val shouldShowRoleSelection: StateFlow<Boolean> = _shouldShowRoleSelection.asStateFlow()

    fun resolveSession() {
        viewModelScope.launch {
            delay(900)
            val profile = authRepository.currentUserProfile()
            if (profile == null) {
                _shouldShowRoleSelection.value = true
            } else {
                _startRole.value = profile.role
            }
        }
    }
}
