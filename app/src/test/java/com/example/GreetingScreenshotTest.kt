package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.FocusTarget
import com.example.ui.FocusGridScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun focus_grid_screenshot() {
    val mockTargets = listOf(
        FocusTarget(id = 1, title = "Master Kotlin Compose", targetDate = "2026-12-15", colorHex = "#FF3366"),
        FocusTarget(id = 2, title = "App Release", targetDate = "2026-09-01", colorHex = "#0A84FF")
    )
    composeTestRule.setContent { 
      MyApplicationTheme(darkTheme = true, dynamicColor = false) { 
        FocusGridScreen(focusTargets = mockTargets) 
      } 
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
