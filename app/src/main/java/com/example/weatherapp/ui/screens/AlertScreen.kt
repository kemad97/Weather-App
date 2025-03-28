package com.example.weatherapp.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.local.AlertType
import com.example.weatherapp.data.local.AlertEntity
import com.example.weatherapp.viewmodel.AlertsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {
    val alerts by viewModel.alerts.collectAsState()
    var showAddDialog by rememberSaveable  { mutableStateOf(false) }
    var selectedStartTime by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var selectedEndTime by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var selectedType by rememberSaveable { mutableStateOf(AlertType.NOTIFICATION) }
    var alertTitle by rememberSaveable { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "Add Alert")
            }
        }
    ) { padding ->
        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No alerts added yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(alerts) { alert ->
                    AlertItem(alert = alert, onDelete = { viewModel.deleteAlert(alert) })
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Weather Alert") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = alertTitle,
                            onValueChange = { alertTitle = it },
                            label = { Text("Alert Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DateTimePicker(
                            label = "Start Time",
                            selectedTime = selectedStartTime,
                            onTimeSelected = { selectedStartTime = it }
                        )

                        DateTimePicker(
                            label = "End Time",
                            selectedTime = selectedEndTime,
                            onTimeSelected = { selectedEndTime = it }
                        )

                        AlertTypeSelector(
                            selectedType = selectedType,
                            onTypeSelected = { selectedType = it }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (alertTitle.isNotBlank()) {
                                viewModel.addAlert(
                                    startTime = selectedStartTime,
                                    endTime = selectedEndTime,
                                    type = selectedType,
                                    title = alertTitle
                                )
                                showAddDialog = false
                                alertTitle = ""
                            }
                        },
                        enabled = alertTitle.isNotBlank()
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DateTimePicker(
    label: String,
    selectedTime: Long,
    onTimeSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = selectedTime

    Column {
        Text(text = label)
        Button(
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)
                                onTimeSelected(calendar.timeInMillis)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        ) {
            Text(formatDateTime(selectedTime))
        }
    }
}

@Composable
fun AlertTypeSelector(
    selectedType: AlertType,
    onTypeSelected: (AlertType) -> Unit
) {
    Column {
        Text("Alert Type")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AlertType.values().forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = type == selectedType,
                        onClick = { onTypeSelected(type) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(type.name)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertItem(
    alert: AlertEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "From: ${formatDateTime(alert.startTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "To: ${formatDateTime(alert.endTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Type: ${alert.type}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete Alert")
            }
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}