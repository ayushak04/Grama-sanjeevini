package com.mindmatrix.gramasanjeevini.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindmatrix.gramasanjeevini.auth.domain.UserRole
import com.mindmatrix.gramasanjeevini.auth.ui.AdminLoginScreen
import com.mindmatrix.gramasanjeevini.auth.ui.ForgotPasswordScreen
import com.mindmatrix.gramasanjeevini.auth.ui.PharmacistLoginScreen
import com.mindmatrix.gramasanjeevini.auth.ui.PharmacistSignupScreen
import com.mindmatrix.gramasanjeevini.auth.ui.RoleSelectionScreen
import com.mindmatrix.gramasanjeevini.auth.ui.SplashScreen
import com.mindmatrix.gramasanjeevini.auth.ui.VillagerLoginScreen
import com.mindmatrix.gramasanjeevini.auth.ui.VillagerSignupScreen
import com.mindmatrix.gramasanjeevini.dashboard.ui.AdminDashboardScreen
import com.mindmatrix.gramasanjeevini.dashboard.ui.PharmacistDashboardScreen
import com.mindmatrix.gramasanjeevini.dashboard.ui.VillagerDashboardScreen

@Composable
fun GramaSanjeeviniNavGraph(
    navController: NavHostController = rememberNavController(),
    darkTheme: Boolean = false,
    onDarkThemeChanged: (Boolean) -> Unit = {},
) {
    NavHost(navController = navController, startDestination = Routes.Splash) {
        composable(Routes.Splash) {
            SplashScreen(
                onRoleSelection = {
                    navController.replaceWith(Routes.RoleSelection)
                },
                onAuthenticatedRole = { role ->
                    navController.replaceWith(role.dashboardRoute())
                },
            )
        }
        composable(Routes.RoleSelection) {
            RoleSelectionScreen(
                onVillagerLogin = { navController.navigate(Routes.VillagerLogin) },
                onVillagerGuest = { navController.navigate(Routes.VillagerGuestDashboard) },
                onPharmacistLogin = { navController.navigate(Routes.PharmacistLogin) },
                onAdminLogin = { navController.navigate(Routes.AdminLogin) },
            )
        }
        composable(Routes.VillagerLogin) {
            VillagerLoginScreen(
                onSignup = { navController.navigate(Routes.VillagerSignup) },
                onForgotPassword = { navController.navigate(Routes.ForgotPassword) },
                onAuthenticated = { navController.replaceWith(Routes.VillagerDashboard) },
                onGuestAccess = { navController.navigate(Routes.VillagerGuestDashboard) },
            )
        }
        composable(Routes.VillagerSignup) {
            VillagerSignupScreen(
                onAuthenticated = { navController.replaceWith(Routes.VillagerDashboard) },
                onLogin = { navController.popBackStack() },
            )
        }
        composable(Routes.PharmacistLogin) {
            PharmacistLoginScreen(
                onSignup = { navController.navigate(Routes.PharmacistSignup) },
                onForgotPassword = { navController.navigate(Routes.ForgotPassword) },
                onAuthenticated = { navController.replaceWith(Routes.PharmacistDashboard) },
            )
        }
        composable(Routes.PharmacistSignup) {
            PharmacistSignupScreen(
                onAuthenticated = { navController.replaceWith(Routes.PharmacistDashboard) },
                onLogin = { navController.popBackStack() },
            )
        }
        composable(Routes.ForgotPassword) {
            ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }
        composable(Routes.AdminLogin) {
            AdminLoginScreen(onAuthenticated = { navController.replaceWith(Routes.AdminDashboard) })
        }
        composable(Routes.VillagerDashboard) {
            VillagerDashboardScreen(
                darkTheme = darkTheme,
                onDarkThemeChanged = onDarkThemeChanged,
                onBackToRoles = { navController.replaceWith(Routes.RoleSelection) },
            )
        }
        composable(Routes.VillagerGuestDashboard) {
            VillagerDashboardScreen(
                darkTheme = darkTheme,
                onDarkThemeChanged = onDarkThemeChanged,
                forceGuestMode = true,
                onBackToRoles = { navController.replaceWith(Routes.RoleSelection) },
            )
        }
        composable(Routes.PharmacistDashboard) {
            PharmacistDashboardScreen(
                darkTheme = darkTheme,
                onDarkThemeChanged = onDarkThemeChanged,
                onLogoutComplete = { navController.replaceWith(Routes.RoleSelection) },
            )
        }
        composable(Routes.AdminDashboard) {
            AdminDashboardScreen(
                darkTheme = darkTheme,
                onDarkThemeChanged = onDarkThemeChanged,
                onLogoutComplete = { navController.replaceWith(Routes.RoleSelection) },
            )
        }
    }
}

private fun UserRole.dashboardRoute(): String = when (this) {
    UserRole.Villager -> Routes.VillagerDashboard
    UserRole.Pharmacist -> Routes.PharmacistDashboard
    UserRole.Admin -> Routes.AdminDashboard
}

private fun NavHostController.replaceWith(route: String) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
