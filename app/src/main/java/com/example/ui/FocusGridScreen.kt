package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.data.FocusTarget
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FocusGridScreen(
    focusTargets: List<FocusTarget>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    val currentYear = today.year

    // Create targets mapped by targetDate (yyyy-MM-dd)
    val targetsByDate = remember(focusTargets) {
        focusTargets.associateBy { it.targetDate }
    }

    // Carousel items list
    val carouselItems = remember(focusTargets, today, currentYear) {
        val list = mutableListOf<CarouselItem>()
        
        // Default 1st item: days remaining for year to end
        val yearEnd = LocalDate.of(currentYear, 12, 31)
        val daysToYearEnd = ChronoUnit.DAYS.between(today, yearEnd).coerceAtLeast(0)
        list.add(
            CarouselItem(
                title = "Year End $currentYear",
                targetDateString = yearEnd.toString(),
                daysRemaining = daysToYearEnd,
                colorHex = "#E0E0E0",
                isDefault = true
            )
        )

        // Subsequent items: user's focus targets
        focusTargets.forEach { target ->
            val targetDate = LocalDate.parse(target.targetDate)
            val daysRem = ChronoUnit.DAYS.between(today, targetDate).coerceAtLeast(0)
            list.add(
                CarouselItem(
                    title = target.title,
                    targetDateString = target.targetDate,
                    daysRemaining = daysRem,
                    colorHex = target.colorHex,
                    isDefault = false,
                    id = target.id
                )
            )
        }
        list
    }

    var showMonthDivision by remember { mutableStateOf(false) }
    var selectedTargetDetail by remember { mutableStateOf<FocusTarget?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000)) // Pure black theme for high-contrast focus
    ) {
        // --- Top Carousel Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Color(0xFF0A0A0A))
                .border(width = 1.dp, color = Color(0xFF1E1E1E), shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .padding(vertical = 12.dp)
        ) {
            val lazyListState = rememberLazyListState()
            
            if (carouselItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No metrics available",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyRow(
                    state = lazyListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("timeline_carousel"),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(carouselItems) { item ->
                        val cardColor = if (item.isDefault) Color(0xFF161616) else Color(0xFF121212)
                        val accentColor = parseHexColor(item.colorHex ?: "#FFFFFF")
                        
                        Card(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .fillMaxHeight()
                                .border(
                                    width = 1.dp,
                                    color = if (item.isDefault) Color(0xFF333333) else accentColor.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Number of days remaining with dim text beside it
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = item.daysRemaining.toString(),
                                        color = if (item.daysRemaining > 0) accentColor else Color.Gray,
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-1.5).sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DAYS LEFT",
                                        color = Color.Gray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }

                                if (!item.isDefault) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    // Title below
                                    Text(
                                        text = item.title.uppercase(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Bottom Grid Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            // Control Panel with only the Months toggle button aligned to the end
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Custom High-Contrast Toggle Switch Row (only the switch pill, no 'Months' text)
                Box(
                    modifier = Modifier
                        .background(Color(0xFF161616), RoundedCornerShape(8.dp))
                        .clickable { showMonthDivision = !showMonthDivision }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("month_toggle_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 16.dp)
                            .background(
                                color = if (showMonthDivision) Color(0xFF00E5FF) else Color(0xFF333333),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(if (showMonthDivision) Alignment.CenterEnd else Alignment.CenterStart)
                                .size(12.dp)
                                .background(Color.White, RoundedCornerShape(6.dp))
                        )
                    }
                }
            }

            // Days Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (showMonthDivision) {
                    // Organization by month
                    val startOfYear = remember { LocalDate.of(currentYear, 1, 1) }
                    val monthsList = remember(currentYear) {
                        (1..12).map { monthVal ->
                            val monthStart = LocalDate.of(currentYear, monthVal, 1)
                            val monthName = monthStart.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH)
                            val daysInMonth = monthStart.lengthOfMonth()
                            val days = (0 until daysInMonth).map { monthStart.plusDays(it.toLong()) }
                            MonthSection(name = monthName, days = days)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().testTag("months_grid_view"),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        monthsList.forEach { monthSection ->
                            item {
                                Text(
                                    text = monthSection.name.uppercase(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            // Calculate month grid items with offset for 7 weekday columns
                            val firstDayOfMonth = monthSection.days.first()
                            val offset = (firstDayOfMonth.dayOfWeek.value % 7)
                            val monthGridItems = List<LocalDate?>(offset) { null } + monthSection.days
                            val chunkedRows = monthGridItems.chunked(7)

                            items(chunkedRows) { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    for (col in 0 until 7) {
                                        val day = rowItems.getOrNull(col)
                                        Box(
                                            modifier = Modifier
                                                .padding(1.5.dp)
                                                .size(9.dp)
                                        ) {
                                            if (day != null) {
                                                DayBox(
                                                    day = day,
                                                    today = today,
                                                    associatedTarget = targetsByDate[day.toString()],
                                                    onClick = {
                                                        val target = targetsByDate[day.toString()]
                                                        if (target != null) {
                                                            selectedTargetDetail = target
                                                        }
                                                    }
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.fillMaxSize())
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Continuous Year Flow Grid - 7 columns representing weekdays
                    val startOfYear = remember { LocalDate.of(currentYear, 1, 1) }
                    val daysInYear = remember(startOfYear) { if (startOfYear.isLeapYear) 366 else 365 }
                    val allDays = remember(startOfYear, daysInYear) {
                        (0 until daysInYear).map { startOfYear.plusDays(it.toLong()) }
                    }

                    val startOffset = (startOfYear.dayOfWeek.value % 7)
                    val continuousGridItems = remember(allDays, startOffset) {
                        List<LocalDate?>(startOffset) { null } + allDays
                    }
                    val chunkedRows = remember(continuousGridItems) {
                        continuousGridItems.chunked(7)
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().testTag("continuous_grid_view"),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(chunkedRows) { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (col in 0 until 7) {
                                    val day = rowItems.getOrNull(col)
                                    Box(
                                        modifier = Modifier
                                            .padding(1.5.dp)
                                            .size(9.dp)
                                    ) {
                                        if (day != null) {
                                            DayBox(
                                                day = day,
                                                today = today,
                                                associatedTarget = targetsByDate[day.toString()],
                                                onClick = {
                                                    val target = targetsByDate[day.toString()]
                                                    if (target != null) {
                                                        selectedTargetDetail = target
                                                    }
                                                }
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.fillMaxSize())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive target info dialog on box tap
    selectedTargetDetail?.let { target ->
        AlertDialog(
            onDismissRequest = { selectedTargetDetail = null },
            title = {
                Text(
                    text = target.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                val remDays = ChronoUnit.DAYS.between(today, LocalDate.parse(target.targetDate)).coerceAtLeast(0)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Target Date: ${target.targetDate}", color = Color.LightGray)
                    Text(
                        text = "$remDays days remaining",
                        color = parseHexColor(target.colorHex),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTargetDetail = null }) {
                    Text("CLOSE", color = Color.White)
                }
            },
            containerColor = Color(0xFF121212),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

@Composable
fun DayBox(
    day: LocalDate,
    today: LocalDate,
    associatedTarget: FocusTarget?,
    onClick: () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }
    val isPast = day.isBefore(today)
    val isToday = day.isEqual(today)
    val isFuture = day.isAfter(today)

    val baseModifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(2.dp))
        .clickable {
            showTooltip = true
            onClick()
        }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isPast -> {
                // Past days are vibrant neon green
                Box(
                    modifier = baseModifier
                        .background(Color(0xFF00FF66))
                        .testTag("day_box_past")
                )
            }
            isToday -> {
                // Today is neon cyan with a white edge highlight
                Box(
                    modifier = baseModifier
                        .background(Color(0xFF00E5FF))
                        .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(2.dp))
                        .testTag("day_box_today")
                )
            }
            isFuture -> {
                if (associatedTarget != null) {
                    // Future target day gets a highlighted neon border of its custom color
                    val borderCol = parseHexColor(associatedTarget.colorHex)
                    Box(
                        modifier = baseModifier
                            .background(Color(0xFF0A0A0A))
                            .border(width = 1.5.dp, color = borderCol, shape = RoundedCornerShape(2.dp))
                            .testTag("day_box_target")
                    )
                } else {
                    // Standard future day is blank/hollow
                    Box(
                        modifier = baseModifier
                            .background(Color(0xFF0F0F0F))
                            .border(width = 0.8.dp, color = Color(0xFF222222), shape = RoundedCornerShape(2.dp))
                            .testTag("day_box_future")
                    )
                }
            }
        }

        if (showTooltip) {
            val weekdayName = remember(day) { day.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH) }
            val monthName = remember(day) { day.month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH) }
            val tooltipText = "$weekdayName, ${day.dayOfMonth} $monthName"

            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, -90), // Display nicely floating above the box
                onDismissRequest = { showTooltip = false },
                properties = PopupProperties(focusable = false)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.8f)), // Neon cyan high-contrast border
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = tooltipText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun showDayToast(context: android.content.Context, day: LocalDate, today: LocalDate) {
    val message = when {
        day.isBefore(today) -> "Past day: ${day.dayOfMonth} ${day.month.name.lowercase()}"
        day.isEqual(today) -> "Today!"
        else -> "${ChronoUnit.DAYS.between(today, day)} days remaining until ${day.dayOfMonth} ${day.month.name.lowercase()}"
    }
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.White
    }
}

data class CarouselItem(
    val title: String,
    val targetDateString: String,
    val daysRemaining: Long,
    val colorHex: String?,
    val isDefault: Boolean,
    val id: Int? = null
)

data class MonthSection(
    val name: String,
    val days: List<LocalDate>
)
