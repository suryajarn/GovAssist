package com.govassist.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.govassist.app.screens.AboutScreen
import com.govassist.app.screens.AccountScreen
import com.govassist.app.screens.ApplicationsScreen
import com.govassist.app.screens.ContinueScreen
import com.govassist.app.screens.HelpScreen
import com.govassist.app.screens.HomeScreen
import com.govassist.app.screens.MenuScreen
import com.govassist.app.screens.ServicesScreen
import com.govassist.app.screens.TextModeScreen

/** Central place listing every screen/route in the app. */
object Routes {
    const val HOME = "home"
    const val MENU = "menu"
    const val ACCOUNT = "account"
    const val TEXT_MODE = "text_mode"
    const val CONTINUE = "continue"
    const val SERVICES = "services"
    const val APPLICATIONS = "applications"
    const val HELP = "help"
    const val ABOUT = "about"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onMenuClick = { navController.navigate(Routes.MENU) },
                onAccountClick = { navController.navigate(Routes.ACCOUNT) },
                onContinueClick = { navController.navigate(Routes.CONTINUE) },
                onTextModeClick = { navController.navigate(Routes.TEXT_MODE) }
            )
        }

        composable(Routes.MENU) {
            MenuScreen(
                onBack = { navController.popBackStack() },
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onServicesClick = { navController.navigate(Routes.SERVICES) },
                onApplicationsClick = { navController.navigate(Routes.APPLICATIONS) },
                onHelpClick = { navController.navigate(Routes.HELP) },
                onAboutClick = { navController.navigate(Routes.ABOUT) }
            )
        }

        composable(Routes.ACCOUNT) {
            AccountScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.TEXT_MODE) {
            TextModeScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.CONTINUE) {
            ContinueScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SERVICES) {
            ServicesScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.APPLICATIONS) {
            ApplicationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.HELP) {
            HelpScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
