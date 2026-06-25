package com.example.ui

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FocusTarget
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTargetsScreen(
    focusTargets: List<FocusTarget>,
    viewModel: FocusTargetViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Curated high-contrast palette
    val colorsPalette = remember {
        listOf(
            "#FF3366", // Neon Red
            "#FF9F0A", // Electric Orange
            "#FFD60A", // Bright Yellow
            "#32D74B", // Vivid Green
            "#0A84FF", // Neon Blue
            "#BF5AF2", // Vibrant Purple
            "#FF375F", // Hot Pink
            "#64D2FF"  // Aqua Cyan
        )
    }

    // Form states
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedColorHex by remember { mutableStateOf(colorsPalette.first()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Edit mode state
    var editingTargetId by remember { mutableStateOf<Int?>(null) }

    // Date picker dialog
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                errorMessage = null
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // "of future only" - set minimum date to tomorrow (today + 1 day)
            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            datePicker.minDate = tomorrow.timeInMillis
        }
    }

    // Handle form submit (Add or Edit)
    val onFormSubmit = {
        val targetDateStr = selectedDate?.toString()
        if (title.isBlank()) {
            errorMessage = "Please enter a target title."
        } else if (targetDateStr == null) {
            errorMessage = "Please select a target date."
        } else {
            if (editingTargetId != null) {
                // Update mode
                viewModel.updateTarget(
                    id = editingTargetId!!,
                    title = title,
                    targetDate = targetDateStr,
                    colorHex = selectedColorHex,
                    onSuccess = {
                        // Reset form
                        title = ""
                        selectedDate = null
                        selectedColorHex = colorsPalette.first()
                        editingTargetId = null
                        errorMessage = null
                        keyboardController?.hide()
                    },
                    onError = { err ->
                        errorMessage = err
                    }
                )
            } else {
                // Add mode
                viewModel.addTarget(
                    title = title,
                    targetDate = targetDateStr,
                    colorHex = selectedColorHex,
                    onSuccess = {
                        // Reset form
                        title = ""
                        selectedDate = null
                        selectedColorHex = colorsPalette.first()
                        errorMessage = null
                        keyboardController?.hide()
                    },
                    onError = { err ->
                        errorMessage = err
                    }
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000)) // Pure dark theme
            .padding(16.dp)
    ) {
        // --- Form Section Header ---
        Text(
            text = if (editingTargetId != null) "EDIT TARGET FOCUS" else "NEW TARGET FOCUS",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // --- Form Body (Card container) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1E1E1E), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Focus Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("What is your focus?", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color(0xFF333333),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("target_title_input"),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )

                // 2. Date Selector (Future Only)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
                        .clickable { datePickerDialog.show() }
                        .padding(14.dp)
                        .testTag("target_date_picker_button"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Calendar",
                            tint = Color.LightGray
                        )
                        Text(
                            text = selectedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                                ?: "Select Target Date (Future Only)",
                            color = if (selectedDate != null) Color.White else Color.Gray,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = "CHANGE",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 3. High-Contrast Color Picker Row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "HIGHLIGHT COLOR",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("color_picker_row"),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colorsPalette.forEach { hex ->
                            val color = parseHexColor(hex)
                            val isSelected = selectedColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedColorHex = hex
                                        errorMessage = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (hex == "#FFD60A") Color.Black else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Error message banner
                AnimatedVisibility(visible = errorMessage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2C0B0E), RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFF8A8A),
                            fontSize = 12.sp
                        )
                    }
                }

                // Submit Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (editingTargetId != null) {
                        OutlinedButton(
                            onClick = {
                                // Cancel editing
                                title = ""
                                selectedDate = null
                                selectedColorHex = colorsPalette.first()
                                editingTargetId = null
                                errorMessage = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFF333333))
                        ) {
                            Text("CANCEL", fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onFormSubmit,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_target_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (editingTargetId != null) "SAVE CHANGES" else "ADD TARGET",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- List Section Header ---
        Text(
            text = "ALL ACTIVE TARGETS (${focusTargets.size})",
            color = Color.LightGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // --- List of active targets ---
        if (focusTargets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No target focuses configured yet.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Use the form above to add a new future target.",
                        color = Color.DarkGray,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("targets_list_view"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(focusTargets, key = { it.id }) { target ->
                    val todayDate = LocalDate.now()
                    val targetDate = LocalDate.parse(target.targetDate)
                    val daysRemaining = ChronoUnit.DAYS.between(todayDate, targetDate).coerceAtLeast(0)
                    val accentColor = parseHexColor(target.colorHex)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF161616), RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF080808)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Color Stripe Indicator
                            Box(
                                modifier = Modifier
                                    .size(width = 4.dp, height = 40.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(accentColor)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Target Info
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = target.title,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = target.targetDate,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "•",
                                        color = Color.DarkGray,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "$daysRemaining days remaining",
                                        color = accentColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Action buttons: Edit and Delete
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        // Load target into form for edit
                                        title = target.title
                                        selectedDate = LocalDate.parse(target.targetDate)
                                        selectedColorHex = target.colorHex
                                        editingTargetId = target.id
                                        errorMessage = null
                                    },
                                    modifier = Modifier.testTag("edit_target_${target.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Target",
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteTarget(target)
                                        // If we were editing this target, clear the edit mode
                                        if (editingTargetId == target.id) {
                                            title = ""
                                            selectedDate = null
                                            selectedColorHex = colorsPalette.first()
                                            editingTargetId = null
                                        }
                                    },
                                    modifier = Modifier.testTag("delete_target_${target.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Target",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
