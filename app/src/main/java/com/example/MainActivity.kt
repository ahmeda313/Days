package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FocusGridScreen
import com.example.ui.FocusTargetViewModel
import com.example.ui.ManageTargetsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: FocusTargetViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true, dynamicColor = false) {
        var selectedTab by remember { mutableStateOf(0) }
        val focusTargets by viewModel.allTargets.collectAsStateWithLifecycle()

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = Color.Black,
          bottomBar = {
            NavigationBar(
              containerColor = Color(0xFF0C0C0C),
              tonalElevation = 0.dp,
              modifier = Modifier.border(
                width = 1.dp,
                color = Color(0xFF1C1C1C),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
              )
            ) {
              NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = {
                  Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Focus Grid"
                  )
                },
                label = { Text("Focus Grid", fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = Color.Black,
                  selectedTextColor = Color.White,
                  indicatorColor = Color.White,
                  unselectedIconColor = Color.Gray,
                  unselectedTextColor = Color.Gray
                ),
                modifier = Modifier.testTag("nav_focus_grid")
              )
              NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = {
                  Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Manage Targets"
                  )
                },
                label = { Text("Manage Targets", fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = Color.Black,
                  selectedTextColor = Color.White,
                  indicatorColor = Color.White,
                  unselectedIconColor = Color.Gray,
                  unselectedTextColor = Color.Gray
                ),
                modifier = Modifier.testTag("nav_manage_targets")
              )
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black)
              .padding(innerPadding)
          ) {
            when (selectedTab) {
              0 -> FocusGridScreen(
                focusTargets = focusTargets
              )
              1 -> ManageTargetsScreen(
                focusTargets = focusTargets,
                viewModel = viewModel
              )
            }
          }
        }
      }
    }
  }
}

