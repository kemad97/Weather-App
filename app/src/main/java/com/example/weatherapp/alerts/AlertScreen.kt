package com.example.weatherapp.alerts

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.utils.AlarmReceiver
import com.example.weatherapp.R
import com.example.weatherapp.model.local.AlertType
import com.example.weatherapp.model.local.AlertEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {
    val alerts by viewModel.alerts.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var selectedStartTime by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var selectedType by rememberSaveable { mutableStateOf(AlertType.NOTIFICATION) }
    var alertTitle by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current


    Scaffold(
        topBar = { TopAppBar(
            title = {Text(stringResource(R.string.weather_alerts))  }
        )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, stringResource(R.string.add_alert))
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
                Text(stringResource(R.string.no_alerts_added_yet))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(alerts) { alert ->
                    AlertItem(alert = alert,
                        onDelete = {
                            viewModel.deleteAlert(alert)
                            cancelAlarm(context, timeMillis = alert.startTime)
                        }
                    )
                }
            }

        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text(stringResource(R.string.add_weather_alert)) },
                text = {
                    var timeError by rememberSaveable { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = alertTitle,
                            onValueChange = { alertTitle = it },
                            label = { Text(stringResource(R.string.alert_title)) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DateTimePicker(
                            label = "Start Time",
                            selectedTime = selectedStartTime,
                            onTimeSelected = {
                                if (it > System.currentTimeMillis()) {
                                    selectedStartTime = it
                                    timeError = false
                                } else {
                                    timeError = true
                                }
                            }
                        )

                        if (timeError) {
                            Text(
                                text = stringResource(R.string.start_time_must_be_in_the_future),
                                color = Color.Red,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
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
                                    type = selectedType,
                                    title = alertTitle
                                )
                                scheduleAlarm(
                                    context,
                                    alertTitle,
                                    selectedStartTime,
                                    type = selectedType
                                )
                                showAddDialog = false
                                alertTitle = ""
                            }
                        },
                        enabled = alertTitle.isNotBlank()
                    ) {
                        Text(stringResource(R.string.add))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                containerColor = Color.DarkGray

            )

        }
    }
}

@SuppressLint("ScheduleExactAlarm")
private fun scheduleAlarm(context: Context, title: String, timeMillis: Long, type: AlertType) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("ALERT_TITLE", title)
        putExtra("ALERT_TYPE", type.name)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        timeMillis.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    if (timeMillis > System.currentTimeMillis()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(timeMillis, pendingIntent),
                    pendingIntent
                )
            } else {
                // Show dialog to request exact alarm permission
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeMillis,
                pendingIntent
            )
        }
    }
}

private fun cancelAlarm(context: Context, timeMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        timeMillis.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}

@Composable
fun DateTimePicker(
    label: String,
    selectedTime: Long,
    onTimeSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, 1)
    calendar.timeInMillis = selectedTime

    Column {
        Text(text = label)
        Button(
            onClick = {
                val datePicker = DatePickerDialog(
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
                )
                datePicker.datePicker.minDate = System.currentTimeMillis()
                datePicker.show()


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
        Text(stringResource(R.string.alert_type))
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
                    text = stringResource(R.string.at, formatDateTime(alert.startTime)),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(R.string.type, alert.type),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, stringResource(R.string.delete_alert))
            }
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}