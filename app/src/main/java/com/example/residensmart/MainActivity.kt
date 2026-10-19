package com.example.residensmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.residensmart.data.model.UserRole
import com.example.residensmart.ui.screens.*
import com.example.residensmart.ui.theme.*
import com.example.residensmart.ui.viewmodel.CondoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CondoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ResidentSmartTheme {
                MainAppShell(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val tabIndex: Int,
    val testTag: String
)

@Composable
fun MainAppShell(
    viewModel: CondoViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    // Handle back button: if not on Home tab (0), return to Home
    BackHandler(enabled = currentTab != 0) {
        viewModel.setTab(0)
    }

    val navItems = remember(currentRole) {
        listOf(
            NavItem("Inicio", Icons.Default.Home, 0, "nav_home"),
            NavItem("Pagos", Icons.Default.ReceiptLong, 1, "nav_payments"),
            NavItem("Reservas", Icons.Default.EventSeat, 2, "nav_bookings"),
            NavItem("Fallas", Icons.Default.ReportProblem, 3, "nav_incidents"),
            NavItem("Directorio", Icons.Default.ContactPhone, 4, "nav_directory")
        ) + if (currentRole == UserRole.ADMIN) {
            listOf(NavItem("Admin", Icons.Default.AdminPanelSettings, 5, "nav_admin"))
        } else {
            emptyList()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CardSurface,
                contentColor = BrandBlue,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(1.dp, EditorialBorder)
                    .navigationBarsPadding()
            ) {
                navItems.forEach { item ->
                    val isSelected = currentTab == item.tabIndex
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(item.tabIndex) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandBlue,
                            selectedTextColor = BrandBlue,
                            indicatorColor = EditorialHighlight,
                            unselectedIconColor = EditorialMuted,
                            unselectedTextColor = EditorialMuted
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.statusBars,
        containerColor = EditorialBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateTab = { viewModel.setTab(it) }
                )
                1 -> PaymentsScreen(viewModel = viewModel)
                2 -> BookingsScreen(viewModel = viewModel)
                3 -> IncidentsScreen(viewModel = viewModel)
                4 -> DirectoryScreen(viewModel = viewModel)
                5 -> AdminScreen(viewModel = viewModel)
                else -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateTab = { viewModel.setTab(it) }
                )
            }
        }
    }
}
